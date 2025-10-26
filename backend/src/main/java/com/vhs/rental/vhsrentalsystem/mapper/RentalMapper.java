package com.vhs.rental.vhsrentalsystem.mapper;

import com.vhs.rental.vhsrentalsystem.dto.RentalResponseDto;
import com.vhs.rental.vhsrentalsystem.model.Rental;
import org.mapstruct.Mapper;

import java.util.List;

/*
 * MapStruct mapper interface for converting between Rental entities and RentalResponseDto.
 * Uses UserMapper and VhsMapper for nested object mapping.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, VhsMapper.class})
public interface RentalMapper {

    RentalResponseDto toDto(Rental rental);

    List<RentalResponseDto> toDtoList(List<Rental> rentalList);

}