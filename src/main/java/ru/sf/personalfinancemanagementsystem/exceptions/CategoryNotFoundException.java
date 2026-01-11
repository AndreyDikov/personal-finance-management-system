package ru.sf.personalfinancemanagementsystem.exceptions;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException() {
        super("Категория не найдена");
    }

}
