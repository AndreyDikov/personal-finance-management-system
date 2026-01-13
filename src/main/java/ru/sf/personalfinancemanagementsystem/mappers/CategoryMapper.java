package ru.sf.personalfinancemanagementsystem.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.sf.personalfinancemanagementsystem.domains.*;
import ru.sf.personalfinancemanagementsystem.dto.requests.CreateCategoryRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.requests.SetBudgetAmountRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.CreateCategoryResponseDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.ViewCategoriesReportResponseDto;
import ru.sf.personalfinancemanagementsystem.entities.CategoryEntity;
import ru.sf.personalfinancemanagementsystem.entities.rows.ExpenseCategoryRow;
import ru.sf.personalfinancemanagementsystem.entities.rows.IncomeCategoryRow;

import java.util.List;


@Mapper(componentModel = ComponentModel.SPRING)
public interface CategoryMapper {

    CategoryDataForCreate toDomain(CreateCategoryRequestDto dto);
    CreateCategoryResponseDto toDto(Category domain);
    Category toDomain(CategoryEntity entity);
    CategoryDataForSetBudgetAmount toDomain(SetBudgetAmountRequestDto dto);
    List<GeneralReport.IncomeCategoryReport> toIncDomains(List<IncomeCategoryRow> entities);
    List<GeneralReport.ExpenseCategoryReport> toExpDomains(List<ExpenseCategoryRow> entities);
    ViewCategoriesReportResponseDto toDto(CategoriesReport domain);

}
