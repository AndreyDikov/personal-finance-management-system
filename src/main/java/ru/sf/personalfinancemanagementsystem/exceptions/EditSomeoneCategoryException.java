package ru.sf.personalfinancemanagementsystem.exceptions;

public class EditSomeoneCategoryException extends RuntimeException {

    public EditSomeoneCategoryException() {
        super("Нельзя редактировать чужую категорию");
    }

}
