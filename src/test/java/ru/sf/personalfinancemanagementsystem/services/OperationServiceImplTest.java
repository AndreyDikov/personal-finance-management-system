package ru.sf.personalfinancemanagementsystem.services;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.sf.personalfinancemanagementsystem.services.impl.OperationServiceImpl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class OperationServiceImplTest {

    private static final UUID USER_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_USER_ID =
            UUID.fromString("99999999-9999-9999-9999-999999999999");
    private static final UUID CATEGORY_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID OPERATION_ID =
            UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final String CATEGORY_NAME = "Еда";
    private static final String DESCRIPTION = "покупка";
    private static final BigDecimal AMOUNT = new BigDecimal("100.00");

    @Mock OperationRepository operationRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock OperationMapper operationMapper;
    @Mock EntityManager entityManager;

    @InjectMocks OperationServiceImpl service;

    @Captor ArgumentCaptor<OperationEntity> operationEntityCaptor;


    @Nested
    @DisplayName("createOperation()")
    class CreateOperation {

        @Test
        @DisplayName("Если категория не найдена — кидает CategoryNotFoundException, операции не сохраняет")
        void shouldThrowCategoryNotFound() {
            OperationDataForCreate data = mock(OperationDataForCreate.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.createOperation(USER_ID, data))
                    .isInstanceOf(CategoryNotFoundException.class);

            verify(categoryRepository).findById(CATEGORY_ID);
            verifyNoInteractions(operationRepository, operationMapper, entityManager);
        }


        @Test
        @DisplayName("Если категория чужая — кидает EditSomeoneCategoryException, операции не сохраняет")
        void shouldThrowEditSomeoneCategory() {
            OperationDataForCreate data = mock(OperationDataForCreate.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);

            CategoryEntity category = mock(CategoryEntity.class);
            when(category.getUserId()).thenReturn(OTHER_USER_ID);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

            assertThatThrownBy(() -> service.createOperation(USER_ID, data))
                    .isInstanceOf(EditSomeoneCategoryException.class);

            verify(categoryRepository).findById(CATEGORY_ID);
            verifyNoInteractions(operationRepository, operationMapper, entityManager);
        }


        @Test
        @DisplayName("Если категория INCOME — сохраняет операцию, refresh делает, баланс/бюджет не считает")
        void shouldReturnResultForIncomeWithoutExtraChecks() {
            OperationDataForCreate data = mock(OperationDataForCreate.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);
            when(data.getAmount()).thenReturn(AMOUNT);
            when(data.getDescription()).thenReturn(DESCRIPTION);

            CategoryEntity category = mock(CategoryEntity.class);
            when(category.getUserId()).thenReturn(USER_ID);
            when(category.getKind()).thenReturn(CategoryKind.INCOME);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

            OperationEntity saved = OperationEntity.builder()
                    .id(OPERATION_ID)
                    .categoryId(CATEGORY_ID)
                    .amount(AMOUNT)
                    .description(DESCRIPTION)
                    .build();

            when(operationRepository.saveAndFlush(any(OperationEntity.class))).thenReturn(saved);
            doNothing().when(entityManager).refresh(saved);
            when(operationMapper.toDomain(saved)).thenReturn(null);

            SavedOperation result = service.createOperation(USER_ID, data);

            assertThat(result).isNotNull();
            assertThat(result.getMessage()).isNull();

            verify(categoryRepository).findById(CATEGORY_ID);

            verify(operationRepository).saveAndFlush(operationEntityCaptor.capture());
            OperationEntity toSave = operationEntityCaptor.getValue();
            assertThat(toSave.getId()).isNull();
            assertThat(toSave.getCategoryId()).isEqualTo(CATEGORY_ID);
            assertThat(toSave.getAmount()).isEqualTo(AMOUNT);
            assertThat(toSave.getDescription()).isEqualTo(DESCRIPTION);

            verify(entityManager).refresh(saved);
            verify(operationMapper).toDomain(saved);

            verify(operationRepository, never()).sumUserBalance(any());
            verify(operationRepository, never()).sumByUserAndCategory(any(), any());
        }


        @Test
        @DisplayName("EXPENSE: бюджет не задан и баланс < 0 — возвращает предупреждение EXPENSES_EXCEEDED_INCOME")
        void shouldWarnWhenBudgetNullAndBalanceNegative() {
            OperationDataForCreate data = mock(OperationDataForCreate.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);
            when(data.getAmount()).thenReturn(AMOUNT);
            when(data.getDescription()).thenReturn(DESCRIPTION);

            CategoryEntity category = mock(CategoryEntity.class);
            when(category.getUserId()).thenReturn(USER_ID);
            when(category.getKind()).thenReturn(CategoryKind.EXPENSE);
            when(category.getBudgetAmount()).thenReturn(null);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

            OperationEntity saved = OperationEntity.builder()
                    .id(OPERATION_ID)
                    .categoryId(CATEGORY_ID)
                    .amount(AMOUNT)
                    .description(DESCRIPTION)
                    .build();

            when(operationRepository.saveAndFlush(any(OperationEntity.class))).thenReturn(saved);
            doNothing().when(entityManager).refresh(saved);
            when(operationMapper.toDomain(saved)).thenReturn(null);

            when(operationRepository.sumUserBalance(USER_ID)).thenReturn(new BigDecimal("-1.00"));

            SavedOperation result = service.createOperation(USER_ID, data);

            assertThat(result.getMessage()).isEqualTo(ResponseMessages.EXPENSES_EXCEEDED_INCOME);

            verify(operationRepository).sumUserBalance(USER_ID);
            verify(operationRepository, never()).sumByUserAndCategory(any(), any());
        }


        @Test
        @DisplayName("EXPENSE: бюджет не задан и баланс >= 0 — без предупреждения")
        void shouldNotWarnWhenBudgetNullAndBalanceNonNegative() {
            OperationDataForCreate data = mock(OperationDataForCreate.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);
            when(data.getAmount()).thenReturn(AMOUNT);

            CategoryEntity category = mock(CategoryEntity.class);
            when(category.getUserId()).thenReturn(USER_ID);
            when(category.getKind()).thenReturn(CategoryKind.EXPENSE);
            when(category.getBudgetAmount()).thenReturn(null);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

            OperationEntity saved = OperationEntity.builder()
                    .id(OPERATION_ID)
                    .categoryId(CATEGORY_ID)
                    .amount(AMOUNT)
                    .build();

            when(operationRepository.saveAndFlush(any(OperationEntity.class))).thenReturn(saved);
            doNothing().when(entityManager).refresh(saved);
            when(operationMapper.toDomain(saved)).thenReturn(null);

            when(operationRepository.sumUserBalance(USER_ID)).thenReturn(BigDecimal.ZERO);

            SavedOperation result = service.createOperation(USER_ID, data);

            assertThat(result.getMessage()).isNull();

            verify(operationRepository).sumUserBalance(USER_ID);
            verify(operationRepository, never()).sumByUserAndCategory(any(), any());
        }


        @Test
        @DisplayName("EXPENSE: бюджет задан, перерасход и баланс >= 0 — сообщение categoryOverBudget")
        void shouldWarnOverBudgetWhenBalanceNonNegative() {
            OperationDataForCreate data = mock(OperationDataForCreate.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);
            when(data.getAmount()).thenReturn(AMOUNT);

            BigDecimal budget = new BigDecimal("500.00");

            CategoryEntity category = mock(CategoryEntity.class);
            when(category.getUserId()).thenReturn(USER_ID);
            when(category.getKind()).thenReturn(CategoryKind.EXPENSE);
            when(category.getBudgetAmount()).thenReturn(budget);
            when(category.getId()).thenReturn(CATEGORY_ID);
            when(category.getName()).thenReturn(CATEGORY_NAME);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

            OperationEntity saved = OperationEntity.builder()
                    .id(OPERATION_ID)
                    .categoryId(CATEGORY_ID)
                    .amount(AMOUNT)
                    .build();

            when(operationRepository.saveAndFlush(any(OperationEntity.class))).thenReturn(saved);
            doNothing().when(entityManager).refresh(saved);
            when(operationMapper.toDomain(saved)).thenReturn(null);

            when(operationRepository.sumUserBalance(USER_ID)).thenReturn(new BigDecimal("10.00"));
            when(operationRepository.sumByUserAndCategory(USER_ID, CATEGORY_ID)).thenReturn(new BigDecimal("600.00"));

            SavedOperation result = service.createOperation(USER_ID, data);

            assertThat(result.getMessage()).isEqualTo(ResponseMessages.categoryOverBudget(CATEGORY_NAME));

            verify(operationRepository).sumUserBalance(USER_ID);
            verify(operationRepository).sumByUserAndCategory(USER_ID, CATEGORY_ID);
        }


        @Test
        @DisplayName("EXPENSE: бюджет задан, перерасход и баланс < 0 — сообщение categoryOverBudgetAndExpensesExceededIncome")
        void shouldWarnOverBudgetAndNegativeBalance() {
            OperationDataForCreate data = mock(OperationDataForCreate.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);
            when(data.getAmount()).thenReturn(AMOUNT);

            BigDecimal budget = new BigDecimal("500.00");

            CategoryEntity category = mock(CategoryEntity.class);
            when(category.getUserId()).thenReturn(USER_ID);
            when(category.getKind()).thenReturn(CategoryKind.EXPENSE);
            when(category.getBudgetAmount()).thenReturn(budget);
            when(category.getId()).thenReturn(CATEGORY_ID);
            when(category.getName()).thenReturn(CATEGORY_NAME);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

            OperationEntity saved = OperationEntity.builder()
                    .id(OPERATION_ID)
                    .categoryId(CATEGORY_ID)
                    .amount(AMOUNT)
                    .build();

            when(operationRepository.saveAndFlush(any(OperationEntity.class))).thenReturn(saved);
            doNothing().when(entityManager).refresh(saved);
            when(operationMapper.toDomain(saved)).thenReturn(null);

            when(operationRepository.sumUserBalance(USER_ID)).thenReturn(new BigDecimal("-10.00"));
            when(operationRepository.sumByUserAndCategory(USER_ID, CATEGORY_ID)).thenReturn(new BigDecimal("600.00"));

            SavedOperation result = service.createOperation(USER_ID, data);

            assertThat(result.getMessage()).isEqualTo(
                    ResponseMessages.categoryOverBudgetAndExpensesExceededIncome(CATEGORY_NAME)
            );

            verify(operationRepository).sumUserBalance(USER_ID);
            verify(operationRepository).sumByUserAndCategory(USER_ID, CATEGORY_ID);
        }


        @Test
        @DisplayName("EXPENSE: бюджет задан, перерасхода нет, но баланс < 0 — сообщение EXPENSES_EXCEEDED_INCOME")
        void shouldWarnNegativeBalanceWhenNotOverBudget() {
            OperationDataForCreate data = mock(OperationDataForCreate.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);
            when(data.getAmount()).thenReturn(AMOUNT);

            BigDecimal budget = new BigDecimal("500.00");

            CategoryEntity category = mock(CategoryEntity.class);
            when(category.getUserId()).thenReturn(USER_ID);
            when(category.getKind()).thenReturn(CategoryKind.EXPENSE);
            when(category.getBudgetAmount()).thenReturn(budget);
            when(category.getId()).thenReturn(CATEGORY_ID);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

            OperationEntity saved = OperationEntity.builder()
                    .id(OPERATION_ID)
                    .categoryId(CATEGORY_ID)
                    .amount(AMOUNT)
                    .build();

            when(operationRepository.saveAndFlush(any(OperationEntity.class))).thenReturn(saved);
            doNothing().when(entityManager).refresh(saved);
            when(operationMapper.toDomain(saved)).thenReturn(null);

            when(operationRepository.sumUserBalance(USER_ID)).thenReturn(new BigDecimal("-1.00"));
            when(operationRepository.sumByUserAndCategory(USER_ID, CATEGORY_ID)).thenReturn(new BigDecimal("100.00"));

            SavedOperation result = service.createOperation(USER_ID, data);

            assertThat(result.getMessage()).isEqualTo(ResponseMessages.EXPENSES_EXCEEDED_INCOME);

            verify(operationRepository).sumUserBalance(USER_ID);
            verify(operationRepository).sumByUserAndCategory(USER_ID, CATEGORY_ID);
        }


        @Test
        @DisplayName("EXPENSE: бюджет задан, перерасхода нет, баланс >= 0 — без сообщений")
        void shouldReturnWithoutWarningsWhenAllGood() {
            OperationDataForCreate data = mock(OperationDataForCreate.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);
            when(data.getAmount()).thenReturn(AMOUNT);

            BigDecimal budget = new BigDecimal("500.00");

            CategoryEntity category = mock(CategoryEntity.class);
            when(category.getUserId()).thenReturn(USER_ID);
            when(category.getKind()).thenReturn(CategoryKind.EXPENSE);
            when(category.getBudgetAmount()).thenReturn(budget);
            when(category.getId()).thenReturn(CATEGORY_ID);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

            OperationEntity saved = OperationEntity.builder()
                    .id(OPERATION_ID)
                    .categoryId(CATEGORY_ID)
                    .amount(AMOUNT)
                    .build();

            when(operationRepository.saveAndFlush(any(OperationEntity.class))).thenReturn(saved);
            doNothing().when(entityManager).refresh(saved);
            when(operationMapper.toDomain(saved)).thenReturn(null);

            when(operationRepository.sumUserBalance(USER_ID)).thenReturn(new BigDecimal("1.00"));
            when(operationRepository.sumByUserAndCategory(USER_ID, CATEGORY_ID)).thenReturn(new BigDecimal("100.00"));

            SavedOperation result = service.createOperation(USER_ID, data);

            assertThat(result.getMessage()).isNull();

            verify(operationRepository).sumUserBalance(USER_ID);
            verify(operationRepository).sumByUserAndCategory(USER_ID, CATEGORY_ID);
        }

    }

}
