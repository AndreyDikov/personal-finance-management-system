package ru.sf.personalfinancemanagementsystem.dto.responses;

import java.util.List;


public record ValidationErrorResponseDto(

        String message,
        List<ValidationError> errors

) {

    public record ValidationError(

            String field,
            String message

    ) {}

}
