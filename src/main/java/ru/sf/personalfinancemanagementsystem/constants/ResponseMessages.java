package ru.sf.personalfinancemanagementsystem.constants;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;


@UtilityClass
public class ResponseMessages {

    public static final String EXPENSES_EXCEEDED_INCOME = "расходы превысили доходы";


    public static @NonNull String categoryOverBudget(
            @NonNull String categoryName
    ) {
        return "превышен бюджет по категории: " + categoryName;
    }


    public static @NonNull String categoryOverBudgetAndExpensesExceededIncome(
            @NonNull String categoryName
    ) {
        return categoryOverBudget(categoryName) + " и " + EXPENSES_EXCEEDED_INCOME;
    }

}
