package ru.sf.personalfinancemanagementsystem.dto.responses;

import java.math.BigDecimal;
import java.util.List;


public record ViewGeneralReportResponseDto(

        BigDecimal generalIncome,
        List<IncomeCategoryReportResponseDto> incomeCategoriesReports,
        BigDecimal generalExpense,
        List<ExpenseCategoryReportResponseDto> expenseCategoriesReports

) {

    public record IncomeCategoryReportResponseDto(

            String categoryName,
            BigDecimal incomeAmount

    ) {}


    public record ExpenseCategoryReportResponseDto(

            String categoryName,
            BigDecimal budgetAmount,
            BigDecimal remainingAmount

    ) {}

}
