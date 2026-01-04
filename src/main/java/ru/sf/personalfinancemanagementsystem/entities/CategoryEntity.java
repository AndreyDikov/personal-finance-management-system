package ru.sf.personalfinancemanagementsystem.entities.tabels;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import ru.sf.personalfinancemanagementsystem.enums.CategoryKind;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "categories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryTable {

    @Id
    @UuidGenerator
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    UUID id;

    @Column(name = "user_id", nullable = false)
    UUID userUuid;

    @Column(name = "name", nullable = false)
    String name;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "kind", nullable = false, columnDefinition = "category_kind")
    CategoryKind kind;

    @Column(name = "budget_amount", precision = 19, scale = 2)
    BigDecimal budgetAmount;

}
