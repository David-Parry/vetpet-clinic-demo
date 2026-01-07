package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtTokenService
 */
@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JwtTokenService jwtTokenService;

    @Test
    void shouldGenerateTokenWithAuthentication() {
        // Given
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(auth.getAuthorities()).thenReturn((Collection) Arrays.asList(
            new SimpleGrantedAuthority("SCOPE_USER"),
            new SimpleGrantedAuthority("SCOPE_MANAGER")
        ));

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mock-token-value");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // When
        String token = jwtTokenService.generateToken(auth);

        // Then
        assertThat(token).isEqualTo("mock-token-value");
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void shouldGenerateTokenWithCustomExpiry() {
        // Given
        String username = "testuser";
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("SCOPE_USER")
        );
        Instant expiresAt = Instant.now().plus(2, ChronoUnit.HOURS);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("custom-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // When
        String token = jwtTokenService.generateToken(username, authorities, expiresAt);

        // Then
        assertThat(token).isEqualTo("custom-token");
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void shouldGenerateTokenWithMultipleAuthorities() {
        // Given
        String username = "admin";
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("SCOPE_USER"),
            new SimpleGrantedAuthority("SCOPE_ADMIN"),
            new SimpleGrantedAuthority("SCOPE_MANAGER")
        );
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("multi-scope-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // When
        String token = jwtTokenService.generateToken(username, authorities, expiresAt);

        // Then
        assertThat(token).isEqualTo("multi-scope-token");
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }
}
