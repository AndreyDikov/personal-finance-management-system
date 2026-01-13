package ru.sf.personalfinancemanagementsystem.entities.rows;

import java.math.BigDecimal;


public interface ExpenseCategoryRow {

    String getCategoryName();
    BigDecimal getBudgetAmount();
    BigDecimal getRemainingAmount();

}
