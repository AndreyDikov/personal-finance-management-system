package ru.sf.personalfinancemanagementsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sf.personalfinancemanagementsystem.domains.Category;
import ru.sf.personalfinancemanagementsystem.entities.CategoryEntity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


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

}
