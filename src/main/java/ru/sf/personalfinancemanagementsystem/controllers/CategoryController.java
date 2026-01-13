package ru.sf.personalfinancemanagementsystem.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sf.personalfinancemanagementsystem.annotations.CurrentUserId;
import ru.sf.personalfinancemanagementsystem.constants.Endpoints;
import ru.sf.personalfinancemanagementsystem.constants.ValidationMessages;
import ru.sf.personalfinancemanagementsystem.domains.CategoriesReport;
import ru.sf.personalfinancemanagementsystem.domains.Category;
import ru.sf.personalfinancemanagementsystem.dto.requests.CreateCategoryRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.requests.SetBudgetAmountRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.CreateCategoryResponseDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.ViewCategoriesReportResponseDto;
import ru.sf.personalfinancemanagementsystem.mappers.CategoryMapper;
import ru.sf.personalfinancemanagementsystem.services.CategoryService;

import java.util.Set;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    CategoryMapper categoryMapper;


    @PostMapping(Endpoints.CREATE_CATEGORY)
    public ResponseEntity<CreateCategoryResponseDto> createCategory(
            @CurrentUserId UUID userId,
            @RequestBody @Valid CreateCategoryRequestDto requestDto
    ) {
        Category newCategory = categoryService.createCategory(
                userId,
                categoryMapper.toDomain(requestDto)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryMapper.toDto(newCategory));
    }


    @PutMapping(Endpoints.SET_BUDGET_AMOUNT)
    public ResponseEntity<Void> setBudgetAmount(
            @CurrentUserId UUID userId,
            @RequestBody @Valid SetBudgetAmountRequestDto requestDto
    ) {
        categoryService.setBudgetAmount(userId, categoryMapper.toDomain(requestDto));

        return ResponseEntity.noContent().build();
    }


    @PostMapping(Endpoints.VIEW_CATEGORIES_REPORT)
    public ResponseEntity<ViewCategoriesReportResponseDto> viewCategoriesReport(
            @CurrentUserId
            UUID userId,

            @Valid
            @RequestBody
            @NotEmpty(message = ValidationMessages.NOT_EMPTY)
            Set<@NotNull(message = ValidationMessages.NOT_NULL) UUID> categoryIds
    ) {
        CategoriesReport categoriesReport = categoryService.getCategoriesReport(
                userId,
                categoryIds
        );

        return ResponseEntity.ok().body(categoryMapper.toDto(categoriesReport));
    }

}
