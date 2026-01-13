package ru.sf.personalfinancemanagementsystem.constants;

import lombok.experimental.UtilityClass;


@UtilityClass
public class ValidationMessages {

    public static final String INPUT_VALIDATION_ERROR = "Ошибка валидации входных данных";
    public static final String INVALID_BODY_REQUEST_FORMAT = "Тело запроса имеет неверный формат";

    public static final String NOT_BLANK = "поле не должно быть пустым";
    public static final String SIZE_MAX = "длина должна быть меньше {max} символов";
    public static final String SIZE_MIN_MAX = "длина должна быть от {min} до {max} символов";
    public static final String NOT_NULL = "обязательное поле";
    public static final String POSITIVE = "поле должно быть больше 0";
    public static final String NOT_EMPTY = "список не должен быть пустым";

    public static final String POSITIVE_BUDGET_AMOUNT = "поле должно быть либо null, либо больше 0";

}
