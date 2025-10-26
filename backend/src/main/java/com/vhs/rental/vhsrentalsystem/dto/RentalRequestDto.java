package com.vhs.rental.vhsrentalsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
 * Data Transfer Object for creating a new Rental.
 * Contains the necessary foreign keys.
 */
@Data
public class RentalRequestDto {

    @NotNull(message = "{rental.userId.notnull}")
    private Long userId; //foreign key used for renting

    @NotNull(message = "{rental.vhsId.notnull}")
    private Long vhsId; //foreign key used for renting
}
