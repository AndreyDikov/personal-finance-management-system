package ru.sf.personalfinancemanagementsystem.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.sf.personalfinancemanagementsystem.domains.User;
import ru.sf.personalfinancemanagementsystem.domains.UserDataForRegister;
import ru.sf.personalfinancemanagementsystem.dto.responses.RegisterResponseDto;
import ru.sf.personalfinancemanagementsystem.entities.UserEntity;


@Mapper(componentModel = ComponentModel.SPRING)
public interface UserMapper {

    RegisterResponseDto toDto(UserDataForRegister domain);

}
