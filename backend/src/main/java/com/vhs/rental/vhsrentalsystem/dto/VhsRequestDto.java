package com.vhs.rental.vhsrentalsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
 * Data Transfer Object for creating a new VHS tape.
 * Contains validated tape details.
 */
@Data
public class VhsRequestDto {
    @NotBlank(message = "{vhs.title.notblank}")
    private String title;

    @NotBlank(message = "{vhs.genre.notblank}")
    private String genre;

    @NotNull(message = "{vhs.releaseYear.notnull}")
    @Min(value = 1900, message = "{vhs.releaseYear.min}")
    private Integer releaseYear;
}
