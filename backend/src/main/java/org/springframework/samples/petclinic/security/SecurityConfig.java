package org.springframework.samples.petclinic.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.DispatcherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.samples.petclinic.auth.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configures security for PetClinic. Ensures that all requests to /graphql are secured.
 *
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final RSAKey rsaKey;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    public SecurityConfig(RSAKeyProvider RSAKeyProvider) {
        this.rsaKey = RSAKeyProvider.getRsaKey();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Support both BCrypt (preferred) and NoOp (for backward compatibility during migration)
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        
        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);
        // Allow NoOp for backward compatibility during migration
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        
        return delegatingPasswordEncoder;
    }

    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException(
                    username
                )
            );
    }

    /**
     * CORS Configuration
     * Allows cross-origin requests from configured origins
     * Origins can be configured via application.properties or environment variables
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse allowed origins from comma-separated string
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        
        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow common headers including Authorization
        configuration.setAllowedHeaders(Arrays.asList(
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.CONTENT_TYPE,
            HttpHeaders.ACCEPT,
            "X-Requested-With"
        ));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("CORS configured with allowed origins: {}", origins);
        
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable CSRF for stateless API
        http.csrf(AbstractHttpConfigurer::disable);

        // Enable CORS with our configuration
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // Stateless session management (JWT-based)
        http.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Configure authorization rules
        http.authorizeHttpRequests(authorizeHttpRequests ->
            authorizeHttpRequests
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                // allow login
                .requestMatchers("/api/login/**").permitAll()
                // allow access to graphiql
                .requestMatchers("/").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/index.html").permitAll()
                .requestMatchers("/graphiql/**").permitAll()
                // ...while all other endpoints (INCLUDING /graphql !) should be authenticated
                //    fine granular, Role-based, access checks are done in the resolver
                .anyRequest().authenticated()
        );

        // Configure OAuth2 Resource Server with JWT
        http.oauth2ResourceServer(c -> c.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * Bearer Token Resolver
     * SECURITY FIX: Disabled token in URL parameters to prevent token leakage in logs
     * Tokens must now be sent in the Authorization header only
     */
    @Bean
    BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
        // SECURITY: Disable token in URL parameters to prevent logging/exposure
        bearerTokenResolver.setAllowUriQueryParameter(false);
        log.info("Bearer token resolver configured - URL parameter tokens DISABLED for security");
        return bearerTokenResolver;
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKeyProvider RSAKeyProvider) {
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwks) {
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }
}
