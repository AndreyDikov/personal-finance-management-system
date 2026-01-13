package ru.sf.personalfinancemanagementsystem.services;

import ru.sf.personalfinancemanagementsystem.domains.CategoriesReport;
import ru.sf.personalfinancemanagementsystem.domains.Category;
import ru.sf.personalfinancemanagementsystem.domains.CategoryDataForCreate;
import ru.sf.personalfinancemanagementsystem.domains.CategoryDataForSetBudgetAmount;

import java.util.Set;
import java.util.UUID;


public interface CategoryService {

    Category createCategory(UUID userId, CategoryDataForCreate data);
    void setBudgetAmount(UUID userId, CategoryDataForSetBudgetAmount data);
    CategoriesReport getCategoriesReport(UUID userId, Set<UUID> categoryIds);

}
