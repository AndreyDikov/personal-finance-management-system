package ru.sf.personalfinancemanagementsystem.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String login) {
        super("Логин уже сущетсвует: " + login);
    }

}
