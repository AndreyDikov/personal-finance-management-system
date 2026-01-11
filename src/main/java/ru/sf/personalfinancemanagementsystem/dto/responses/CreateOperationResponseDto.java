package ru.sf.personalfinancemanagementsystem.dto.responses;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;


public record CreateOperationResponseDto(

    String message,
    Operation operation

) {

    public record Operation(

            UUID id,
            UUID categoryId,
            BigDecimal amount,
            String description,
            OffsetDateTime happenedAt

    ){}

}
