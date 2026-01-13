package ru.sf.personalfinancemanagementsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sf.personalfinancemanagementsystem.entities.OperationEntity;
import ru.sf.personalfinancemanagementsystem.entities.rows.ExpenseCategoryRow;
import ru.sf.personalfinancemanagementsystem.entities.rows.IncomeCategoryRow;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, UUID> {

    @Query(value = """
            select coalesce(sum(
                case
                    when c.kind = 'INCOME' then o.amount
                    else -o.amount
                end
            ), 0)
            from operations o
            join categories c on c.id = o.category_id
            where c.user_id = :userId
            """, nativeQuery = true)
    BigDecimal sumUserBalance(UUID userId);


    @Query(value = """
            select coalesce(sum(o.amount), 0)
            from operations o
            join categories c on c.id = o.category_id
            where c.user_id = :userId
              and c.id = :categoryId
            """, nativeQuery = true)
    BigDecimal sumByUserAndCategory(UUID userId, UUID categoryId);


    @Query(value = """
            select coalesce(sum(o.amount), 0)
            from operations o
            join categories c on c.id = o.category_id
            where c.user_id = :userId
              and c.kind = 'INCOME'
            """, nativeQuery = true)
    BigDecimal totalIncome(UUID userId);


    @Query(value = """
            select coalesce(sum(o.amount), 0)
            from operations o
            join categories c on c.id = o.category_id
            where c.user_id = :userId
              and c.kind = 'EXPENSE'
            """, nativeQuery = true)
    BigDecimal totalExpense(UUID userId);

}
