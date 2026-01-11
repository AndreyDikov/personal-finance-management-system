package ru.sf.personalfinancemanagementsystem.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.sf.personalfinancemanagementsystem.constants.ValidationMessages;

import java.math.BigDecimal;
import java.util.UUID;


public record CreateOperationRequestDto(

        @NotNull(message = ValidationMessages.NOT_NULL)
        UUID categoryId,

        @NotNull(message = ValidationMessages.NOT_NULL)
        @Positive(message = ValidationMessages.POSITIVE)
        BigDecimal amount,

        String description

) {}
