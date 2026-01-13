package ru.sf.personalfinancemanagementsystem.dto.responses;

import java.util.List;


public record ViewCategoriesReportResponseDto(

        List<ViewGeneralReportResponseDto.IncomeCategoryReportResponseDto> incomeCategories,
        List<ViewGeneralReportResponseDto.ExpenseCategoryReportResponseDto> expenseCategories

) {}
