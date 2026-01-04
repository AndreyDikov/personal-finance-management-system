package ru.sf.personalfinancemanagementsystem.requests;

import jakarta.validation.constraints.Size;


public record CredentialsRequest(

        @Size(min = 1, max = 128)
        String login,

        @Size(min = 4, max = 72)
        String password

) {}
