package ru.sf.personalfinancemanagementsystem.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "operations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OperationEntity {

    @Id
    @UuidGenerator
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    UUID id;

    @Column(name = "category_id", nullable = false)
    UUID categoryUuid;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    BigDecimal amount;

    @Column(name = "description")
    String description;

    @Generated
    @Column(
            name = "happened_at",
            nullable = false,
            columnDefinition = "timestamptz",
            insertable = false,
            updatable = false
    )
    OffsetDateTime happenedAt;

}
