package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JwtTokenService jwtTokenService;

    @Test
    void generateToken_shouldGenerateTokenWithUserRole() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("SCOPE_USER")
        );
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mock-jwt-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        String token = jwtTokenService.generateToken(authentication);

        // Then
        assertThat(token).isEqualTo("mock-jwt-token");
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void generateToken_shouldGenerateTokenWithManagerRole() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("manager");
        
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("SCOPE_MANAGER")
        );
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mock-manager-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        String token = jwtTokenService.generateToken(authentication);

        // Then
        assertThat(token).isEqualTo("mock-manager-token");
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void generateToken_shouldIncludeMultipleAuthorities() {
        // Given
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("SCOPE_USER"),
            new SimpleGrantedAuthority("SCOPE_MANAGER")
        );
        
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mock-multi-scope-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        String token = jwtTokenService.generateToken("testuser", authorities, Instant.now().plus(1, ChronoUnit.HOURS));

        // Then
        assertThat(token).isEqualTo("mock-multi-scope-token");
        ArgumentCaptor<JwtEncoderParameters> paramsCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, times(1)).encode(paramsCaptor.capture());
        
        // Verify the scope claim contains both authorities
        JwtClaimsSet claims = paramsCaptor.getValue().getClaims();
        String scopeClaim = (String) claims.getClaim("scope");
        assertThat(scopeClaim).contains("SCOPE_USER");
        assertThat(scopeClaim).contains("SCOPE_MANAGER");
    }

    @Test
    void generateToken_shouldSetCorrectExpiration() {
        // Given
        Instant expiresAt = Instant.now().plus(2, ChronoUnit.HOURS);
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("SCOPE_USER")
        );
        
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mock-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        jwtTokenService.generateToken("testuser", authorities, expiresAt);

        // Then
        ArgumentCaptor<JwtEncoderParameters> paramsCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, times(1)).encode(paramsCaptor.capture());
        
        JwtClaimsSet claims = paramsCaptor.getValue().getClaims();
        assertThat(claims.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void generateToken_shouldSetCorrectSubject() {
        // Given
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("SCOPE_USER")
        );
        
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mock-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        jwtTokenService.generateToken("johndoe", authorities, Instant.now().plus(1, ChronoUnit.HOURS));

        // Then
        ArgumentCaptor<JwtEncoderParameters> paramsCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, times(1)).encode(paramsCaptor.capture());
        
        JwtClaimsSet claims = paramsCaptor.getValue().getClaims();
        assertThat(claims.getSubject()).isEqualTo("johndoe");
    }
}
