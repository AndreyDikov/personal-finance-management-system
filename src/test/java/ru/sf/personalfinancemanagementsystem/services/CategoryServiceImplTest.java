package ru.sf.personalfinancemanagementsystem.services;

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
import ru.sf.personalfinancemanagementsystem.domains.CategoriesReport;
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
import ru.sf.personalfinancemanagementsystem.services.impl.CategoryServiceImpl;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CategoryServiceImplTest {

    private static final UUID USER_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_USER_ID =
            UUID.fromString("99999999-9999-9999-9999-999999999999");
    private static final UUID CATEGORY_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final String CATEGORY_NAME = "Еда";
    private static final BigDecimal POSITIVE_BUDGET = new BigDecimal("100.00");

    @Mock CategoryRepository categoryRepository;
    @Mock CategoryMapper categoryMapper;

    @InjectMocks CategoryServiceImpl service;

    @Captor ArgumentCaptor<CategoryEntity> categoryEntityCaptor;


    @Nested
    @DisplayName("createCategory()")
    class CreateCategory {

        @Test
        @DisplayName("Если kind=INCOME и budgetAmount != null — кидает BudgetForIncomeCategoryException, репозиторий не трогает")
        void shouldThrowBudgetForIncomeWhenBudgetProvided() {
            CategoryDataForCreate data = mock(CategoryDataForCreate.class);
            when(data.getKind()).thenReturn(CategoryKind.INCOME);
            when(data.getBudgetAmount()).thenReturn(POSITIVE_BUDGET);

            assertThatThrownBy(() -> service.createCategory(USER_ID, data))
                    .isInstanceOf(BudgetForIncomeCategoryException.class);

            verifyNoInteractions(categoryRepository, categoryMapper);
        }


        @Test
        @SuppressWarnings({"rawtypes", "unchecked"})
        @DisplayName("Если категория уже существует — кидает CategoryAlreadyExistsException и не сохраняет")
        void shouldThrowAlreadyExistsWhenSameNameExists() {
            CategoryDataForCreate data = mock(CategoryDataForCreate.class);
            when(data.getKind()).thenReturn(CategoryKind.EXPENSE);
            when(data.getName()).thenReturn(CATEGORY_NAME);

            Optional present = Optional.of(new Object());
            when(categoryRepository.findByUserIdAndName(USER_ID, CATEGORY_NAME)).thenReturn(present);

            assertThatThrownBy(() -> service.createCategory(USER_ID, data))
                    .isInstanceOf(CategoryAlreadyExistsException.class);

            verify(categoryRepository).findByUserIdAndName(USER_ID, CATEGORY_NAME);
            verify(categoryRepository, never()).save(any(CategoryEntity.class));
            verifyNoInteractions(categoryMapper);
        }


        @Test
        @DisplayName("Если всё ок — сохраняет CategoryEntity с нужными полями и вызывает mapper.toDomain")
        void shouldSaveEntityAndMapToDomain() {
            CategoryDataForCreate data = mock(CategoryDataForCreate.class);
            when(data.getKind()).thenReturn(CategoryKind.EXPENSE);
            when(data.getBudgetAmount()).thenReturn(POSITIVE_BUDGET);
            when(data.getName()).thenReturn(CATEGORY_NAME);

            when(categoryRepository.findByUserIdAndName(USER_ID, CATEGORY_NAME)).thenReturn(Optional.empty());

            UUID savedId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            CategoryEntity savedEntity = CategoryEntity.builder()
                    .id(savedId)
                    .userId(USER_ID)
                    .name(CATEGORY_NAME)
                    .kind(CategoryKind.EXPENSE)
                    .budgetAmount(POSITIVE_BUDGET)
                    .build();

            when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(savedEntity);

            when(categoryMapper.toDomain(savedEntity)).thenReturn(null);

            service.createCategory(USER_ID, data);

            verify(categoryRepository).findByUserIdAndName(USER_ID, CATEGORY_NAME);

            verify(categoryRepository).save(categoryEntityCaptor.capture());
            CategoryEntity toSave = categoryEntityCaptor.getValue();

            assertThat(toSave.getId()).isNull();
            assertThat(toSave.getUserId()).isEqualTo(USER_ID);
            assertThat(toSave.getName()).isEqualTo(CATEGORY_NAME);
            assertThat(toSave.getKind()).isEqualTo(CategoryKind.EXPENSE);
            assertThat(toSave.getBudgetAmount()).isEqualTo(POSITIVE_BUDGET);

            verify(categoryMapper).toDomain(savedEntity);
            verifyNoMoreInteractions(categoryRepository, categoryMapper);
        }

    }


    @Nested
    @DisplayName("setBudgetAmount()")
    class SetBudgetAmount {

        @Test
        @DisplayName("Если категория не найдена — кидает CategoryNotFoundException и не делает update")
        void shouldThrowCategoryNotFoundWhenCategoryMissing() {
            CategoryDataForSetBudgetAmount data = mock(CategoryDataForSetBudgetAmount.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.setBudgetAmount(USER_ID, data))
                    .isInstanceOf(CategoryNotFoundException.class);

            verify(categoryRepository).findById(CATEGORY_ID);
            verify(categoryRepository, never()).setBudgetAmount(any(UUID.class), any());
            verifyNoInteractions(categoryMapper);
        }


        @Test
        @DisplayName("Если пытаются редактировать чужую категорию — кидает EditSomeoneCategoryException и не делает update")
        void shouldThrowEditSomeoneCategoryWhenUserMismatch() {
            CategoryDataForSetBudgetAmount data = mock(CategoryDataForSetBudgetAmount.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);

            CategoryEntity entity = mock(CategoryEntity.class);
            when(entity.getUserId()).thenReturn(OTHER_USER_ID);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(entity));

            assertThatThrownBy(() -> service.setBudgetAmount(USER_ID, data))
                    .isInstanceOf(EditSomeoneCategoryException.class);

            verify(categoryRepository).findById(CATEGORY_ID);
            verify(categoryRepository, never()).setBudgetAmount(any(UUID.class), any());
            verifyNoInteractions(categoryMapper);
        }


        @Test
        @DisplayName("Если kind=INCOME и budgetAmount != null — кидает BudgetForIncomeCategoryException и не делает update")
        void shouldThrowBudgetForIncomeWhenBudgetProvided() {
            CategoryDataForSetBudgetAmount data = mock(CategoryDataForSetBudgetAmount.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);
            when(data.getBudgetAmount()).thenReturn(POSITIVE_BUDGET);

            CategoryEntity entity = mock(CategoryEntity.class);
            when(entity.getUserId()).thenReturn(USER_ID);
            when(entity.getKind()).thenReturn(CategoryKind.INCOME);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(entity));

            assertThatThrownBy(() -> service.setBudgetAmount(USER_ID, data))
                    .isInstanceOf(BudgetForIncomeCategoryException.class);

            verify(categoryRepository).findById(CATEGORY_ID);
            verify(categoryRepository, never()).setBudgetAmount(any(UUID.class), any());
            verifyNoInteractions(categoryMapper);
        }


        @Test
        @DisplayName("Если всё ок (EXPENSE) — вызывает repository.setBudgetAmount с нужными параметрами")
        void shouldUpdateBudgetForExpense() {
            CategoryDataForSetBudgetAmount data = mock(CategoryDataForSetBudgetAmount.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);
            when(data.getBudgetAmount()).thenReturn(POSITIVE_BUDGET);

            CategoryEntity entity = mock(CategoryEntity.class);
            when(entity.getUserId()).thenReturn(USER_ID);
            when(entity.getKind()).thenReturn(CategoryKind.EXPENSE);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(entity));

            service.setBudgetAmount(USER_ID, data);

            verify(categoryRepository).findById(CATEGORY_ID);
            verify(categoryRepository).setBudgetAmount(CATEGORY_ID, POSITIVE_BUDGET);
            verifyNoInteractions(categoryMapper);
        }


        @Test
        @DisplayName("Если всё ок (INCOME и budgetAmount = null) — вызывает repository.setBudgetAmount с null")
        void shouldUpdateBudgetForIncomeWhenNullBudget() {
            CategoryDataForSetBudgetAmount data = mock(CategoryDataForSetBudgetAmount.class);
            when(data.getCategoryId()).thenReturn(CATEGORY_ID);
            when(data.getBudgetAmount()).thenReturn(null);

            CategoryEntity entity = mock(CategoryEntity.class);
            when(entity.getUserId()).thenReturn(USER_ID);
            when(entity.getKind()).thenReturn(CategoryKind.INCOME);

            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(entity));

            service.setBudgetAmount(USER_ID, data);

            verify(categoryRepository).findById(CATEGORY_ID);
            verify(categoryRepository).setBudgetAmount(CATEGORY_ID, null);
            verifyNoInteractions(categoryMapper);
        }

    }


    @Nested
    @DisplayName("getCategoriesReport()")
    class GetCategoriesReport {

        private static final UUID USER_ID =
                UUID.fromString("11111111-1111-1111-1111-111111111111");
        private static final UUID CAT_1 =
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        private static final UUID CAT_2 =
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");


        @Test
        @DisplayName("Если хотя бы одной категории нет — кидает CategoryNotFoundException и не строит отчет")
        void shouldThrowCategoryNotFound() {
            Set<UUID> ids = Set.of(CAT_1, CAT_2);

            when(categoryRepository.findExistingIds(ids)).thenReturn(Set.of(CAT_1));

            assertThatThrownBy(() -> service.getCategoriesReport(USER_ID, ids))
                    .isInstanceOf(CategoryNotFoundException.class);

            verify(categoryRepository).findExistingIds(ids);
            verify(categoryRepository, never()).findOwnedIds(any(), any());

            verify(categoryRepository, never()).incomeByCategories(any(), any());
            verify(categoryRepository, never()).expenseCategoriesRemaining(any(), any());
            verifyNoInteractions(categoryMapper);
        }


        @Test
        @DisplayName("Если хотя бы одна категория не принадлежит пользователю — кидает EditSomeoneCategoryException")
        void shouldThrowEditSomeoneCategory() {
            Set<UUID> ids = Set.of(CAT_1, CAT_2);

            when(categoryRepository.findExistingIds(ids)).thenReturn(Set.of(CAT_1, CAT_2));
            when(categoryRepository.findOwnedIds(USER_ID, ids)).thenReturn(Set.of(CAT_1));

            assertThatThrownBy(() -> service.getCategoriesReport(USER_ID, ids))
                    .isInstanceOf(EditSomeoneCategoryException.class);

            verify(categoryRepository).findExistingIds(ids);
            verify(categoryRepository).findOwnedIds(USER_ID, ids);

            verify(categoryRepository, never()).incomeByCategories(any(), any());
            verify(categoryRepository, never()).expenseCategoriesRemaining(any(), any());
            verifyNoInteractions(categoryMapper);
        }


        @Test
        @DisplayName("Если все категории существуют и принадлежат пользователю — возвращает отчет и вызывает мапперы")
        void shouldReturnReport() {
            Set<UUID> ids = Set.of(CAT_1, CAT_2);

            when(categoryRepository.findExistingIds(ids)).thenReturn(Set.of(CAT_1, CAT_2));
            when(categoryRepository.findOwnedIds(USER_ID, ids)).thenReturn(Set.of(CAT_1, CAT_2));

            IncomeCategoryRow incomeRow = mock(IncomeCategoryRow.class);
            ExpenseCategoryRow expenseRow = mock(ExpenseCategoryRow.class);

            var incomeRows = List.of(incomeRow);
            var expenseRows = List.of(expenseRow);

            when(categoryRepository.incomeByCategories(USER_ID, ids)).thenReturn(incomeRows);
            when(categoryRepository.expenseCategoriesRemaining(USER_ID, ids)).thenReturn(expenseRows);

            when(categoryMapper.toIncDomains(incomeRows)).thenReturn(Collections.emptyList());
            when(categoryMapper.toExpDomains(expenseRows)).thenReturn(Collections.emptyList());

            CategoriesReport report = service.getCategoriesReport(USER_ID, ids);

            assertThat(report).isNotNull();
            assertThat(report.getIncomeCategories()).isEmpty();
            assertThat(report.getExpenseCategories()).isEmpty();

            var inOrder = inOrder(categoryRepository, categoryMapper);
            inOrder.verify(categoryRepository).findExistingIds(ids);
            inOrder.verify(categoryRepository).findOwnedIds(USER_ID, ids);
            inOrder.verify(categoryRepository).incomeByCategories(USER_ID, ids);
            inOrder.verify(categoryRepository).expenseCategoriesRemaining(USER_ID, ids);
            inOrder.verify(categoryMapper).toIncDomains(incomeRows);
            inOrder.verify(categoryMapper).toExpDomains(expenseRows);

            verifyNoMoreInteractions(categoryRepository, categoryMapper);
        }

    }

}
