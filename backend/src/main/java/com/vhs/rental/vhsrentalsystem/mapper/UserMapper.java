package com.vhs.rental.vhsrentalsystem.mapper;

import com.vhs.rental.vhsrentalsystem.dto.UserRequestDto;
import com.vhs.rental.vhsrentalsystem.dto.UserResponseDto;
import com.vhs.rental.vhsrentalsystem.model.User;
import org.mapstruct.Mapper;

import java.util.List;

/*
 * MapStruct mapper interface for converting between User entities and DTOs
 * (UserRequestDto, UserResponseDto).
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);

    User toEntity(UserRequestDto userRequestDto);

    List<UserResponseDto> toDtoList(List<User> userList);
}