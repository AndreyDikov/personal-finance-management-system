package ru.sf.personalfinancemanagementsystem.controllers;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sf.personalfinancemanagementsystem.annotations.CurrentUserId;
import ru.sf.personalfinancemanagementsystem.constants.Endpoints;
import ru.sf.personalfinancemanagementsystem.domains.Category;
import ru.sf.personalfinancemanagementsystem.dto.requests.CreateCategoryRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.requests.SetBudgetAmountRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.CreateCategoryResponseDto;
import ru.sf.personalfinancemanagementsystem.mappers.CategoryMapper;
import ru.sf.personalfinancemanagementsystem.services.CategoryService;

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

}
