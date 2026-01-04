package ru.sf.personalfinancemanagementsystem.responses;

import java.util.UUID;


public record RegisterResponse(

        UUID userId,
        String login

) {}
