package ru.sf.personalfinancemanagementsystem.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.sf.personalfinancemanagementsystem.domains.Category;
import ru.sf.personalfinancemanagementsystem.domains.CategoryDataForCreate;
import ru.sf.personalfinancemanagementsystem.domains.CategoryDataForSetBudgetAmount;
import ru.sf.personalfinancemanagementsystem.domains.GeneralReport;
import ru.sf.personalfinancemanagementsystem.dto.requests.CreateCategoryRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.requests.SetBudgetAmountRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.CreateCategoryResponseDto;
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

}
