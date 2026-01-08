package ru.sf.personalfinancemanagementsystem.dto.responses;

import java.util.UUID;


public record RegisterResponseDto(

        UUID id,
        String login

) {}
