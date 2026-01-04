package ru.sf.personalfinancemanagementsystem.dto.responses;

import java.time.Instant;


public record TokenResponseDto(

        String token,
        Instant expiresAt

) {}
