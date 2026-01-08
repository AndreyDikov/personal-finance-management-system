package ru.sf.personalfinancemanagementsystem.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import ru.sf.personalfinancemanagementsystem.domains.Token;
import ru.sf.personalfinancemanagementsystem.domains.UserDataForToken;
import ru.sf.personalfinancemanagementsystem.services.impl.JwtServiceImpl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class JwtServiceImplTest {

    private static final String ISSUER = "https://pfms";
    private static final long TTL_SECONDS = 3600L;
    private static final String LOGIN = "bogdan";
    private static final String TOKEN_VALUE = "jwt.token.value";
    private static final String ALG_HEADER_VALUE = "HS256";

    @Mock YamlService yamlService;
    @Mock JwtEncoder jwtEncoder;

    @Captor ArgumentCaptor<JwtEncoderParameters> jwtEncoderParametersCaptor;

    @InjectMocks JwtServiceImpl service;


    @Nested
    @DisplayName("issue()")
    class Issue {

        @Test
        @DisplayName("Формирует claims, ставит HS256 и возвращает токен с expiresAt")
        void shouldBuildClaimsAndSignWithHs256() {
            when(yamlService.getIssuer()).thenReturn(ISSUER);
            when(yamlService.getTtlSeconds()).thenReturn(TTL_SECONDS);

            UUID userId = UUID.randomUUID();
            UserDataForToken user = UserDataForToken.builder()
                    .id(userId)
                    .login(LOGIN)
                    .build();

            Instant now = Instant.now();
            Jwt encoded = new Jwt(
                    TOKEN_VALUE,
                    now,
                    now.plus(TTL_SECONDS, ChronoUnit.SECONDS),
                    Map.of("alg", ALG_HEADER_VALUE),
                    Map.of(
                            "sub", userId.toString(),
                            "login", LOGIN
                    )
            );

            when(jwtEncoder.encode(jwtEncoderParametersCaptor.capture())).thenReturn(encoded);

            Instant before = Instant.now();
            Token result = service.issue(user);
            Instant after = Instant.now();

            assertThat(result.getToken()).isEqualTo(TOKEN_VALUE);

            Instant minExp = before.plus(TTL_SECONDS, ChronoUnit.SECONDS);
            Instant maxExp = after.plus(TTL_SECONDS, ChronoUnit.SECONDS);
            assertThat(result.getExpiresAt()).isBetween(minExp, maxExp);

            JwtEncoderParameters params = jwtEncoderParametersCaptor.getValue();
            JwtClaimsSet claims = params.getClaims();

            assertThat(claims.getIssuer().toString()).isEqualTo(ISSUER);
            assertThat(claims.getSubject()).isEqualTo(userId.toString());
            assertThat(claims.getClaimAsString("login")).isEqualTo(LOGIN);
            assertThat(claims.getIssuedAt()).isNotNull();
            assertThat(claims.getExpiresAt()).isBetween(minExp, maxExp);

            JwsHeader header = params.getJwsHeader();
            assert header != null;
            assertThat(header.getAlgorithm()).isEqualTo(MacAlgorithm.HS256);

            verify(yamlService).getIssuer();
            verify(yamlService).getTtlSeconds();
            verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
        }

    }

}
