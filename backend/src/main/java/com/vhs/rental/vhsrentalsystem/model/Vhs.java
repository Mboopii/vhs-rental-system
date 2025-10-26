package com.vhs.rental.vhsrentalsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.Objects;

/*
 * Represents a VHS tape entity.
 * Mapped to the 'vhs' table in the database.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Vhs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Title cannot be blank.")
    private String title;

    @NotBlank(message = "Genre cannot be blank.")
    private String genre;

    @Min(value = 1900, message = "Release year must be after 1900.")
    private int releaseYear;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vhs vhs = (Vhs) o;
        return id != null && id.equals(vhs.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
