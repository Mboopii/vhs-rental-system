package com.vhs.rental.vhsrentalsystem.mapper;

import com.vhs.rental.vhsrentalsystem.dto.VhsRequestDto;
import com.vhs.rental.vhsrentalsystem.dto.VhsResponseDto;
import com.vhs.rental.vhsrentalsystem.model.Vhs;
import org.mapstruct.Mapper;

import java.util.List;

/*
 * MapStruct mapper interface for converting between Vhs entities and DTOs
 * (VhsRequestDto, VhsResponseDto).
 */
@Mapper(componentModel = "spring")
public interface VhsMapper {

    VhsResponseDto toDto(Vhs vhs);

    Vhs toEntity(VhsRequestDto vhsRequestDto);

    List<VhsResponseDto> toDtoList(List<Vhs> vhsList);
}