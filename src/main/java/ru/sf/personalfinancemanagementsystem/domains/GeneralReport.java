package ru.sf.personalfinancemanagementsystem.domains;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneralReport {

    BigDecimal generalIncome;
    List<IncomeCategoryReport> incomeCategoriesReports;
    BigDecimal generalExpense;
    List<ExpenseCategoryReport> expenseCategoriesReports;


    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class IncomeCategoryReport {

        String categoryName;
        BigDecimal incomeAmount;

    }


    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ExpenseCategoryReport {

        String categoryName;
        BigDecimal budgetAmount;
        BigDecimal remainingAmount;

    }

}
