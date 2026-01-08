package ru.sf.personalfinancemanagementsystem.exceptions;

public class SecretSizeException extends IllegalStateException {

    public SecretSizeException() {
        super("Секрет должен быть минимум 32 байта для HS256");
    }

}
