package ru.sf.personalfinancemanagementsystem.controllers;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sf.personalfinancemanagementsystem.annotations.CurrentUserId;
import ru.sf.personalfinancemanagementsystem.constants.Endpoints;
import ru.sf.personalfinancemanagementsystem.domains.GeneralReport;
import ru.sf.personalfinancemanagementsystem.domains.SavedOperation;
import ru.sf.personalfinancemanagementsystem.dto.requests.CreateOperationRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.CreateOperationResponseDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.ViewGeneralReportResponseDto;
import ru.sf.personalfinancemanagementsystem.mappers.OperationMapper;
import ru.sf.personalfinancemanagementsystem.services.OperationService;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OperationController {

    OperationService operationService;

    OperationMapper operationMapper;


    @PostMapping(Endpoints.CREATE_OPERATION)
    public ResponseEntity<CreateOperationResponseDto> createOperation(
            @CurrentUserId UUID userId,
            @RequestBody @Valid CreateOperationRequestDto requestDto
    ) {
        SavedOperation savedOperation = operationService.createOperation(
                userId,
                operationMapper.toDomain(requestDto)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(operationMapper.toDto(savedOperation));
    }


    @GetMapping(Endpoints.VIEW_GENERAL_REPORT)
    public ResponseEntity<ViewGeneralReportResponseDto> viewGeneralReport(
            @CurrentUserId UUID userId
    ) {
        GeneralReport report = operationService.getGeneralReport(userId);

        return ResponseEntity.ok().body(operationMapper.toDto(report));
    }

}
