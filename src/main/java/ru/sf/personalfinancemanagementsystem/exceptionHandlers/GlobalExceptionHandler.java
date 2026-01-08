package ru.sf.personalfinancemanagementsystem.exceptionHandlers;

import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.sf.personalfinancemanagementsystem.constants.ValidationMessages;
import ru.sf.personalfinancemanagementsystem.dto.responses.ErrorResponseDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.ValidationErrorResponseDto;
import ru.sf.personalfinancemanagementsystem.exceptions.BadLoginOrPasswordException;
import ru.sf.personalfinancemanagementsystem.exceptions.UserAlreadyExistsException;

import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponseDto handleMethodArgumentNotValid(
            @NotNull MethodArgumentNotValidException exception
    ) {
        List<ValidationErrorResponseDto.ValidationError> errors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(GlobalExceptionHandler::mapFieldError)
                .toList();

        return new ValidationErrorResponseDto(
                ValidationMessages.INPUT_VALIDATION_ERROR,
                errors
        );
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ValidationErrorResponseDto handleConstraintViolation(
            @NotNull ConstraintViolationException exception
    ) {
        List<ValidationErrorResponseDto.ValidationError> errors = exception
                .getConstraintViolations()
                .stream()
                .map(violation -> new ValidationErrorResponseDto
                                .ValidationError(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .toList();

        return new ValidationErrorResponseDto(
                ValidationMessages.INPUT_VALIDATION_ERROR,
                errors
        );
    }


    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
            BadLoginOrPasswordException.class,
            UserAlreadyExistsException.class
    })
    public ErrorResponseDto handleShortLinkExceptions(
            @NotNull RuntimeException exception
    ) {
        return new ErrorResponseDto(exception.getMessage());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponseDto handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception
    ) {
        return new ErrorResponseDto(ValidationMessages.INVALID_BODY_REQUEST_FORMAT);
    }


    private static @NotNull ValidationErrorResponseDto.ValidationError mapFieldError(
            @NotNull FieldError fieldError
    ) {
        return new ValidationErrorResponseDto.ValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage()
        );
    }

}
