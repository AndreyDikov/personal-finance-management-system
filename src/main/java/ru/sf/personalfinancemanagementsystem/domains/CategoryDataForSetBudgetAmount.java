package ru.sf.personalfinancemanagementsystem.domains;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDataForSetBudgetAmount {

    UUID categoryId;
    BigDecimal budgetAmount;

}
