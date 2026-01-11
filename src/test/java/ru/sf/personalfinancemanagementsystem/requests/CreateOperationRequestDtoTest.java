package ru.sf.personalfinancemanagementsystem.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.sf.personalfinancemanagementsystem.dto.requests.CreateOperationRequestDto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@FieldDefaults(level = AccessLevel.PRIVATE)
class CreateOperationRequestDtoTest {

    private static final UUID VALID_CATEGORY_ID =
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final BigDecimal POSITIVE_AMOUNT = new BigDecimal("100.00");
    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO;
    private static final BigDecimal NEGATIVE_AMOUNT = new BigDecimal("-1.00");

    Validator validator;


    @BeforeEach
    void init() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }


    @ParameterizedTest(name = "{1}")
    @MethodSource("invalidRequests")
    @DisplayName("Невалидные запросы: должна быть одна ошибка на ожидаемом поле")
    void invalidRequest(
            CreateOperationRequestDto request,
            String testName,
            String invalidField
    ) {
        Set<ConstraintViolation<CreateOperationRequestDto>> violations = validator.validate(request);

        assertThat(violations).satisfiesExactly(fieldViolation(invalidField));
    }


    @MethodSource("validRequests")
    @ParameterizedTest(name = "{1}")
    @DisplayName("Валидные запросы: ошибок быть не должно")
    void validRequest(
            CreateOperationRequestDto request,
            String testName
    ) {
        Set<ConstraintViolation<CreateOperationRequestDto>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }


    private static @NonNull Stream<Arguments> invalidRequests() {
        return Stream.of(
                Arguments.of(request(null, POSITIVE_AMOUNT, "desc"), "categoryId = null", "categoryId"),
                Arguments.of(request(VALID_CATEGORY_ID, null, "desc"), "amount = null", "amount"),
                Arguments.of(request(VALID_CATEGORY_ID, ZERO_AMOUNT, "desc"), "amount = 0", "amount"),
                Arguments.of(request(VALID_CATEGORY_ID, NEGATIVE_AMOUNT, "desc"), "amount < 0", "amount")
        );
    }


    private static @NonNull Stream<Arguments> validRequests() {
        return Stream.of(
                Arguments.of(request(VALID_CATEGORY_ID, POSITIVE_AMOUNT, null), "description = null (допустимо)"),
                Arguments.of(request(VALID_CATEGORY_ID, POSITIVE_AMOUNT, "покупка"), "description задано")
        );
    }


    private static @NonNull CreateOperationRequestDto request(
            UUID categoryId,
            BigDecimal amount,
            String description
    ) {
        return new CreateOperationRequestDto(categoryId, amount, description);
    }


    private static @NonNull Consumer<ConstraintViolation<CreateOperationRequestDto>> fieldViolation(
            String field
    ) {
        return violation -> assertThat(violation.getPropertyPath())
                .satisfiesOnlyOnce(node -> assertThat(node.getName()).isEqualTo(field));
    }

}
