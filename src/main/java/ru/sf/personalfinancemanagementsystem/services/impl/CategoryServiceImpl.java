package ru.sf.personalfinancemanagementsystem.services.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sf.personalfinancemanagementsystem.domains.CategoriesReport;
import ru.sf.personalfinancemanagementsystem.domains.Category;
import ru.sf.personalfinancemanagementsystem.domains.CategoryDataForCreate;
import ru.sf.personalfinancemanagementsystem.domains.CategoryDataForSetBudgetAmount;
import ru.sf.personalfinancemanagementsystem.entities.CategoryEntity;
import ru.sf.personalfinancemanagementsystem.entities.rows.ExpenseCategoryRow;
import ru.sf.personalfinancemanagementsystem.entities.rows.IncomeCategoryRow;
import ru.sf.personalfinancemanagementsystem.enums.CategoryKind;
import ru.sf.personalfinancemanagementsystem.exceptions.BudgetForIncomeCategoryException;
import ru.sf.personalfinancemanagementsystem.exceptions.CategoryAlreadyExistsException;
import ru.sf.personalfinancemanagementsystem.exceptions.CategoryNotFoundException;
import ru.sf.personalfinancemanagementsystem.exceptions.EditSomeoneCategoryException;
import ru.sf.personalfinancemanagementsystem.mappers.CategoryMapper;
import ru.sf.personalfinancemanagementsystem.repositories.CategoryRepository;
import ru.sf.personalfinancemanagementsystem.services.CategoryService;
import ru.sf.personalfinancemanagementsystem.utils.Checks;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    CategoryMapper categoryMapper;


    @Override
    @Transactional
    public Category createCategory(
            UUID userId,
            @NonNull CategoryDataForCreate data
    ) {
        Checks.begin()
                .check(data.getKind().equals(CategoryKind.INCOME)
                                && data.getBudgetAmount() != null,
                        BudgetForIncomeCategoryException::new)
                .check(categoryRepository.findByUserIdAndName(userId, data.getName())
                                .isPresent(),
                        CategoryAlreadyExistsException::new);

        CategoryEntity entity = CategoryEntity.builder()
                .userId(userId)
                .name(data.getName())
                .kind(data.getKind())
                .budgetAmount(data.getBudgetAmount())
                .build();

        CategoryEntity savedCategoryEntity = categoryRepository.save(entity);

        return categoryMapper.toDomain(savedCategoryEntity);
    }


    @Override
    @Transactional
    public void setBudgetAmount(
            @NonNull UUID userId,
            @NonNull CategoryDataForSetBudgetAmount data
    ) {
        CategoryEntity entity = categoryRepository.findById(data.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        Checks.begin()
                .check(!userId.equals(entity.getUserId()),
                        EditSomeoneCategoryException::new)
                .check(entity.getKind().equals(CategoryKind.INCOME)
                                && data.getBudgetAmount() != null,
                        BudgetForIncomeCategoryException::new);

        categoryRepository.setBudgetAmount(
                data.getCategoryId(),
                data.getBudgetAmount()
        );
    }


    @Override
    @Transactional(readOnly = true)
    public CategoriesReport getCategoriesReport(
            UUID userId,
            Set<UUID> categoryIds
    ) {
        boolean isNotFoundCategories =
                categoryRepository.findExistingIds(categoryIds).size() < categoryIds.size();
        Checks.begin().check(isNotFoundCategories, CategoryNotFoundException::new);

        boolean isSomeoneUserCategories =
                categoryRepository.findOwnedIds(userId, categoryIds).size() < categoryIds.size();
        Checks.begin().check(isSomeoneUserCategories, EditSomeoneCategoryException::new);

        List<IncomeCategoryRow> incomeCategoryRows = categoryRepository
                .incomeByCategories(userId, categoryIds);
        List<ExpenseCategoryRow> expenseCategoryRows = categoryRepository
                .expenseCategoriesRemaining(userId, categoryIds);

        return CategoriesReport.builder()
                .incomeCategories(categoryMapper.toIncDomains(incomeCategoryRows))
                .expenseCategories(categoryMapper.toExpDomains(expenseCategoryRows))
                .build();
    }

}
