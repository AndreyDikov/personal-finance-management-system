package ru.sf.personalfinancemanagementsystem.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.sf.personalfinancemanagementsystem.domains.GeneralReport;
import ru.sf.personalfinancemanagementsystem.domains.OperationDataForCreate;
import ru.sf.personalfinancemanagementsystem.domains.SavedOperation;
import ru.sf.personalfinancemanagementsystem.dto.requests.CreateOperationRequestDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.CreateOperationResponseDto;
import ru.sf.personalfinancemanagementsystem.dto.responses.ViewGeneralReportResponseDto;
import ru.sf.personalfinancemanagementsystem.entities.OperationEntity;


@Mapper(componentModel = ComponentModel.SPRING)
public interface OperationMapper {

    CreateOperationResponseDto toDto(SavedOperation domain);
    OperationDataForCreate toDomain(CreateOperationRequestDto dto);
    SavedOperation.Operation toDomain(OperationEntity entity);
    ViewGeneralReportResponseDto toDto(GeneralReport domain);

}
