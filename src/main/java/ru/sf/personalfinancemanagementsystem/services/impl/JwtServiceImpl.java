package ru.sf.personalfinancemanagementsystem.services.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import ru.sf.personalfinancemanagementsystem.domains.UserDataForToken;
import ru.sf.personalfinancemanagementsystem.domains.Token;
import ru.sf.personalfinancemanagementsystem.services.JwtService;
import ru.sf.personalfinancemanagementsystem.services.YamlService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtServiceImpl implements JwtService {

    YamlService yamlService;

    JwtEncoder jwtEncoder;


    @Override
    public Token issue(@NotNull UserDataForToken userDataForToken) {
        Instant now = Instant.now();
        Instant exp = now.plus(yamlService.getTtlSeconds(), ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(yamlService.getIssuer())
                .issuedAt(now)
                .expiresAt(exp)
                .subject(userDataForToken.getId().toString())
                .claim("login", userDataForToken.getLogin())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();

        return Token.builder()
                .token(token)
                .expiresAt(exp)
                .build();
    }

}
