package com.vhs.rental.vhsrentalsystem.dto;

import lombok.Data;

/*
 * Data Transfer Object for representing VHS tape information in API responses.
 */
@Data
public class VhsResponseDto {
    private Long id;
    private String title;
    private String genre;
    private int releaseYear;
}