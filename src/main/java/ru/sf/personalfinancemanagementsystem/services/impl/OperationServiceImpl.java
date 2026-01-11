package ru.sf.personalfinancemanagementsystem.services.impl;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sf.personalfinancemanagementsystem.constants.ResponseMessages;
import ru.sf.personalfinancemanagementsystem.domains.OperationDataForCreate;
import ru.sf.personalfinancemanagementsystem.domains.SavedOperation;
import ru.sf.personalfinancemanagementsystem.entities.CategoryEntity;
import ru.sf.personalfinancemanagementsystem.entities.OperationEntity;
import ru.sf.personalfinancemanagementsystem.enums.CategoryKind;
import ru.sf.personalfinancemanagementsystem.exceptions.CategoryNotFoundException;
import ru.sf.personalfinancemanagementsystem.exceptions.EditSomeoneCategoryException;
import ru.sf.personalfinancemanagementsystem.mappers.OperationMapper;
import ru.sf.personalfinancemanagementsystem.repositories.CategoryRepository;
import ru.sf.personalfinancemanagementsystem.repositories.OperationRepository;
import ru.sf.personalfinancemanagementsystem.services.OperationService;

import java.math.BigDecimal;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OperationServiceImpl implements OperationService {

    OperationRepository operationRepository;
    CategoryRepository categoryRepository;

    OperationMapper operationMapper;

    EntityManager entityManager;


    @Override
    @Transactional
    public SavedOperation createOperation(
            UUID userId,
            @NonNull OperationDataForCreate data
    ) {
        CategoryEntity categoryEntity = categoryRepository
                .findById(data.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        if (!categoryEntity.getUserId().equals(userId)) {
            throw new EditSomeoneCategoryException();
        }

        OperationEntity operation = OperationEntity.builder()
                .categoryId(data.getCategoryId())
                .amount(data.getAmount())
                .description(data.getDescription())
                .build();

        OperationEntity savedOperation = operationRepository.saveAndFlush(operation);
        entityManager.refresh(savedOperation);

        SavedOperation result = SavedOperation.builder()
                .operation(operationMapper.toDomain(savedOperation))
                .build();

        if (categoryEntity.getKind().equals(CategoryKind.INCOME)) {
            return result;
        }

        BigDecimal sumUserBalance = operationRepository.sumUserBalance(userId);

        if (categoryEntity.getBudgetAmount() == null) {
            if (sumUserBalance.compareTo(BigDecimal.ZERO) < 0) {
                result.setMessage(ResponseMessages.EXPENSES_EXCEEDED_INCOME);
            }
            return result;
        }

        BigDecimal sumByUserAndCategory = operationRepository.sumByUserAndCategory(
                userId,
                categoryEntity.getId()
        );

        boolean overBudget = sumByUserAndCategory
                .compareTo(categoryEntity.getBudgetAmount()) > 0;
        boolean balanceNegative = sumUserBalance.compareTo(BigDecimal.ZERO) < 0;

        if (overBudget && !balanceNegative) {
            result.setMessage(ResponseMessages.categoryOverBudget(categoryEntity.getName()));
            return result;
        }

        if (overBudget) {
            result.setMessage(ResponseMessages.categoryOverBudgetAndExpensesExceededIncome(
                    categoryEntity.getName()
            ));
            return result;
        }

        if (balanceNegative) {
            result.setMessage(ResponseMessages.EXPENSES_EXCEEDED_INCOME);
        }

        return result;
    }

}
