package ru.sf.personalfinancemanagementsystem.exceptions;

import org.springframework.security.authentication.BadCredentialsException;


public class BadLoginOrPasswordException extends BadCredentialsException {

    public BadLoginOrPasswordException() {
        super("Неправильный логин или пароль");
    }

}
