package ru.sf.personalfinancemanagementsystem.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.sf.personalfinancemanagementsystem.dto.requests.CredentialsRequestDto;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@FieldDefaults(level = AccessLevel.PRIVATE)
public class CredentialsRequestDtoTest {

    private static final String VALID_LOGIN = "bogdan";
    private static final String VALID_PASSWORD = "StrongPass1";

    Validator validator;


    @BeforeEach
    void init() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }


    @MethodSource
    @ParameterizedTest(name = "{1}")
    void invalidRequest(
            CredentialsRequestDto request,
            String testName,
            String expectedField
    ) {
        Set<ConstraintViolation<CredentialsRequestDto>> violations = validator.validate(request);
        assertThat(violations).satisfiesExactly(fieldViolation(expectedField));
    }


    @MethodSource
    @ParameterizedTest(name = "{1}")
    void validRequest(
            CredentialsRequestDto request,
            String testName
    ) {
        Set<ConstraintViolation<CredentialsRequestDto>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }


    private static @NonNull Stream<Arguments> invalidRequest() {
        return Stream.of(
                Arguments.of(dto(null, VALID_PASSWORD), "логин = null", "login"),
                Arguments.of(dto("   ", VALID_PASSWORD), "логин состоит только из пробелов", "login"),
                Arguments.of(dto(repeat('a', 129), VALID_PASSWORD), "логин длиннее 128 символов", "login"),

                Arguments.of(dto(VALID_LOGIN, null), "пароль = null", "password"),
                Arguments.of(dto(VALID_LOGIN, repeat(' ', 8)), "пароль состоит только из пробелов", "password"),
                Arguments.of(dto(VALID_LOGIN, repeat('a', 7)), "пароль короче 8 символов", "password"),
                Arguments.of(dto(VALID_LOGIN, repeat('a', 73)), "пароль длиннее 72 символов", "password")
        );
    }


    private static @NonNull Stream<Arguments> validRequest() {
        return Stream.of(
                Arguments.of(dto(VALID_LOGIN, VALID_PASSWORD), "валидные логин и пароль"),
                Arguments.of(dto(repeat('a', 128), repeat('a', 8)), "границы: логин=128, пароль=8"),
                Arguments.of(dto("user.name-1", repeat('a', 72)), "границы: пароль=72")
        );
    }


    private static @NonNull CredentialsRequestDto dto(
            String login,
            String password
    ) {
        return new CredentialsRequestDto(login, password);
    }


    private static @NonNull String repeat(
            char ch,
            int count
    ) {
        return String.valueOf(ch).repeat(Math.max(0, count));
    }


    private static @NonNull Consumer<ConstraintViolation<CredentialsRequestDto>> fieldViolation(
            String expectedField
    ) {
        return violation -> assertThat(violation.getPropertyPath())
                .satisfiesOnlyOnce(node -> assertThat(node.getName()).isEqualTo(expectedField));
    }

}
