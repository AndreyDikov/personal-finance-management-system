package ru.sf.personalfinancemanagementsystem.responses;

import java.time.Instant;


public record TokenResponse(

        String token,
        Instant expiresAt

) {}
