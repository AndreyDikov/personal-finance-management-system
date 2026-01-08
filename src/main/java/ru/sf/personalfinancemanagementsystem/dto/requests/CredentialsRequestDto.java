package ru.sf.personalfinancemanagementsystem.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.sf.personalfinancemanagementsystem.constants.ValidationMessages;


public record CredentialsRequestDto(

        @NotBlank(message = ValidationMessages.NOT_BLANK_LOGIN)
        @Size(max = 128, message = ValidationMessages.SIZE_LOGIN)
        String login,

        @NotBlank(message = ValidationMessages.NOT_BLANK_PASSWORD)
        @Size(min = 8, max = 72, message = ValidationMessages.SIZE_PASSWORD)
        String password

) {}
