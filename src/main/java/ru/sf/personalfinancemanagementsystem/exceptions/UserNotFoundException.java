package ru.sf.personalfinancemanagementsystem.exceptions;

import org.springframework.security.authentication.BadCredentialsException;

public class UserNotFoundException extends BadCredentialsException {

    public UserNotFoundException() {
        super("Пользователь не найден");
    }

}
