package ru.sf.personalfinancemanagementsystem.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.sf.personalfinancemanagementsystem.constants.ValidationMessages;

import java.math.BigDecimal;
import java.util.UUID;


public record SetBudgetAmountRequestDto(

        @NotNull(message = ValidationMessages.NOT_NULL)
        UUID categoryId,

        @Positive(message = ValidationMessages.POSITIVE_BUDGET_AMOUNT)
        BigDecimal budgetAmount

) {}
