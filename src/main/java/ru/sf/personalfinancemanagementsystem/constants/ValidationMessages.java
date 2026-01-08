package ru.sf.personalfinancemanagementsystem.constants;

import lombok.experimental.UtilityClass;


@UtilityClass
public class ValidationMessages {

    public static final String INPUT_VALIDATION_ERROR = "Ошибка валидации входных данных";
    public static final String INVALID_BODY_REQUEST_FORMAT = "Тело запроса имеет неверный формат";

    public static final String LOGIN = "login: ";
    public static final String NOT_BLANK_LOGIN = LOGIN + "не должен быть пустым";
    public static final String SIZE_LOGIN = LOGIN + "длина должна быть меньше {max} символов";

    public static final String PASSWORD = "password: ";
    public static final String NOT_BLANK_PASSWORD = PASSWORD + "не должен быть пустым";
    public static final String SIZE_PASSWORD = PASSWORD + "длина должна быть от {min} до {max} символов";

}
