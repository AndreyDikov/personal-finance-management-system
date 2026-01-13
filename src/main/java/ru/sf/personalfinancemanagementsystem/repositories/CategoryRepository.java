package ru.sf.personalfinancemanagementsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sf.personalfinancemanagementsystem.domains.Category;
import ru.sf.personalfinancemanagementsystem.entities.CategoryEntity;
import ru.sf.personalfinancemanagementsystem.entities.rows.ExpenseCategoryRow;
import ru.sf.personalfinancemanagementsystem.entities.rows.IncomeCategoryRow;

import java.math.BigDecimal;
import java.util.*;


@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    @Query(value = """
            select id
                 , user_id
                 , name
                 , kind
                 , budget_amount
            from categories
            where user_id = :userId
                and name = :categoryName
            """, nativeQuery = true)
    Optional<CategoryEntity> findByUserIdAndName(UUID userId, String categoryName);


    @Query(value = """
            update categories
            set budget_amount = :budgetAmount
            where id = :categoryId
            """, nativeQuery = true)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void setBudgetAmount(UUID categoryId, BigDecimal budgetAmount);


    @Query(value = """
            select c.name as categoryName
                 , coalesce(sum(o.amount), 0) as incomeAmount
            from categories c
            left join operations o on o.category_id = c.id
            where c.user_id = :userId
              and c.kind = 'INCOME'
            group by c.name
            order by c.name
            """, nativeQuery = true)
    List<IncomeCategoryRow> incomeByCategories(UUID userId);


    @Query(value = """
            select c.name as categoryName
                 , c.budget_amount as budgetAmount
                 , (c.budget_amount - coalesce(sum(o.amount), 0)) as remainingAmount
            from categories c
            left join operations o on o.category_id = c.id
            where c.user_id = :userId
              and c.kind = 'EXPENSE'
              and c.budget_amount is not null
            group by c.name, c.budget_amount
            order by c.name
            """, nativeQuery = true)
    List<ExpenseCategoryRow> expenseCategoriesRemaining(UUID userId);


    @Query(value = """
            select c.name as categoryName
                 , coalesce(sum(o.amount), 0) as incomeAmount
            from categories c
            left join operations o on o.category_id = c.id
            where c.user_id = :userId
              and c.kind = 'INCOME'
              and c.id in (:categoryIds)
            group by c.name
            order by c.name
            """, nativeQuery = true)
    List<IncomeCategoryRow> incomeByCategories(UUID userId, Collection<UUID> categoryIds);


    @Query(value = """
            select c.name as categoryName
                 , c.budget_amount as budgetAmount
                 , (c.budget_amount - coalesce(sum(o.amount), 0)) as remainingAmount
            from categories c
            left join operations o on o.category_id = c.id
            where c.user_id = :userId
              and c.kind = 'EXPENSE'
              and c.budget_amount is not null
              and c.id in (:categoryIds)
            group by c.name, c.budget_amount
            order by c.name
            """, nativeQuery = true)
    List<ExpenseCategoryRow> expenseCategoriesRemaining(
            UUID userId,
            Collection<UUID> categoryIds
    );


    @Query(value = """
        select c.id
        from categories c
        where c.id in (:categoryIds)
        """, nativeQuery = true)
    Set<UUID> findExistingIds(Set<UUID> categoryIds);


    @Query(value = """
        select c.id
        from categories c
        where c.user_id = :userId
          and c.id in (:categoryIds)
        """, nativeQuery = true)
    Set<UUID> findOwnedIds(UUID userId, Set<UUID> categoryIds);

}
