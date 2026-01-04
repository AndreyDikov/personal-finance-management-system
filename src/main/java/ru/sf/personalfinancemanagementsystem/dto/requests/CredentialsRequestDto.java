package ru.sf.personalfinancemanagementsystem.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CredentialsRequestDto(

        @NotBlank
        @Size(max = 128)
        String login,

        @NotBlank
        @Size(min = 8, max = 72)
        String password

) {}
