package ru.sf.personalfinancemanagementsystem.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.sf.personalfinancemanagementsystem.domains.Credentials;
import ru.sf.personalfinancemanagementsystem.dto.requests.CredentialsRequestDto;


@Mapper(componentModel = ComponentModel.SPRING)
public interface CredentialsMapper {

    Credentials toDomain(CredentialsRequestDto dto);

}
