package ru.sf.personalfinancemanagementsystem.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.sf.personalfinancemanagementsystem.domains.Token;
import ru.sf.personalfinancemanagementsystem.dto.responses.TokenResponseDto;


@Mapper(componentModel = ComponentModel.SPRING)
public interface TokenMapper {

    TokenResponseDto toDto(Token domain);

}
