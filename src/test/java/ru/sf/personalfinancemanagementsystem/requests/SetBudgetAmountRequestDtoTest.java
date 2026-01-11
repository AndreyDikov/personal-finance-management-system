package ru.sf.personalfinancemanagementsystem.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.sf.personalfinancemanagementsystem.dto.requests.SetBudgetAmountRequestDto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@FieldDefaults(level = AccessLevel.PRIVATE)
class SetBudgetAmountRequestDtoTest {

    private static final UUID VALID_CATEGORY_ID =
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private static final BigDecimal POSITIVE_BUDGET = new BigDecimal("100.00");
    private static final BigDecimal ZERO_BUDGET = BigDecimal.ZERO;
    private static final BigDecimal NEGATIVE_BUDGET = new BigDecimal("-1.00");

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
            SetBudgetAmountRequestDto request,
            String testName,
            String invalidField
    ) {
        Set<ConstraintViolation<SetBudgetAmountRequestDto>> violations = validator.validate(request);

        assertThat(violations).satisfiesExactly(fieldViolation(invalidField));
    }


    @MethodSource("validRequests")
    @ParameterizedTest(name = "{1}")
    @DisplayName("Валидные запросы: ошибок быть не должно")
    void validRequest(
            SetBudgetAmountRequestDto request,
            String testName
    ) {
        Set<ConstraintViolation<SetBudgetAmountRequestDto>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }


    private static @NonNull Stream<Arguments> invalidRequests() {
        return Stream.of(
                Arguments.of(request(null, null), "categoryId = null", "categoryId"),
                Arguments.of(request(VALID_CATEGORY_ID, ZERO_BUDGET), "budgetAmount = 0", "budgetAmount"),
                Arguments.of(request(VALID_CATEGORY_ID, NEGATIVE_BUDGET), "budgetAmount < 0", "budgetAmount")
        );
    }


    private static @NonNull Stream<Arguments> validRequests() {
        return Stream.of(
                Arguments.of(request(VALID_CATEGORY_ID, null), "budgetAmount = null (допустимо)"),
                Arguments.of(request(VALID_CATEGORY_ID, POSITIVE_BUDGET), "budgetAmount > 0")
        );
    }


    private static @NonNull SetBudgetAmountRequestDto request(
            UUID categoryId,
            BigDecimal budgetAmount
    ) {
        return new SetBudgetAmountRequestDto(categoryId, budgetAmount);
    }


    private static @NonNull Consumer<ConstraintViolation<SetBudgetAmountRequestDto>> fieldViolation(
            String field
    ) {
        return violation -> assertThat(violation.getPropertyPath())
                .satisfiesOnlyOnce(node -> assertThat(node.getName()).isEqualTo(field));
    }

}
