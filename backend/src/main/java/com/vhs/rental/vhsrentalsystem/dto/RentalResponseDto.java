package com.vhs.rental.vhsrentalsystem.dto;

import lombok.Data;
import java.time.LocalDate;

/*
 * Data Transfer Object for representing Rental information in API responses.
 * Includes nested DTOs for related User and Vhs details.
 */
@Data
public class RentalResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Double lateFee;
    private UserResponseDto user;
    private VhsResponseDto vhs;
}