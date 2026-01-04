package ru.sf.personalfinancemanagementsystem.controllers;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sf.personalfinancemanagementsystem.constants.Endpoints;
import ru.sf.personalfinancemanagementsystem.domains.Token;
import ru.sf.personalfinancemanagementsystem.domains.UserDataForRegister;
import ru.sf.personalfinancemanagementsystem.dto.requests.CredentialsRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.RegisterResponseDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.TokenResponseDto;
import ru.sf.personalfinancemanagementsystem.mappers.CredentialsMapper;
import ru.sf.personalfinancemanagementsystem.mappers.TokenMapper;
import ru.sf.personalfinancemanagementsystem.mappers.UserMapper;
import ru.sf.personalfinancemanagementsystem.services.AuthenticationService;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    UserMapper userMapper;
    CredentialsMapper credentialsMapper;
    TokenMapper tokenMapper;


    @PostMapping(Endpoints.REGISTER)
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid CredentialsRequestDto request) {
        UserDataForRegister userData = authenticationService.register(credentialsMapper.toDomain(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(userData));
    }


    @PostMapping(Endpoints.GET_TOKEN)
    public ResponseEntity<TokenResponseDto> token(@RequestBody @Valid CredentialsRequestDto request) {
        Token token = authenticationService.issueToken(credentialsMapper.toDomain(request));

        return ResponseEntity.ok(tokenMapper.toDto(token));
    }

}
