package ru.sf.personalfinancemanagementsystem.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sf.personalfinancemanagementsystem.constants.Endpoints;
import ru.sf.personalfinancemanagementsystem.dto.requests.CredentialsRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.RegisterResponseDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.TokenResponseDto;
import ru.sf.personalfinancemanagementsystem.services.impl.AuthenticationServiceImpl;

import java.net.URI;


@RestController
@RequestMapping(Endpoints.AUTH)
public class AuthController {

    private final AuthenticationServiceImpl authenticationServiceImpl;

    public AuthController(AuthenticationServiceImpl authenticationServiceImpl) {
        this.authenticationServiceImpl = authenticationServiceImpl;
    }

    @PostMapping(Endpoints.REGISTER)
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid CredentialsRequestDto req) {
        var user = authenticationServiceImpl.register(req);
        var body = new RegisterResponseDto(user.getId(), user.getLogin());
        return ResponseEntity
                .created(URI.create("/api/users/" + user.getId()))
                .body(body);
    }

    @PostMapping(Endpoints.GET_TOKEN)
    public ResponseEntity<TokenResponseDto> token(@RequestBody @Valid CredentialsRequestDto req) {
        var token = authenticationServiceImpl.issueToken(req);
        return ResponseEntity.ok(new TokenResponseDto(token.value(), token.expiresAt()));
    }
}