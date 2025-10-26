package com.vhs.rental.vhsrentalsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

/*
 * Represents a Rental transaction entity.
 * Maps the relationship between a User, a Vhs tape, and rental dates.
 * Mapped to the 'rental' table in the database.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User must be provided.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @NotNull(message = "VHS must be provided.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vhs_id", nullable = false)
    private Vhs vhs;

    @Column(nullable = false)
    private LocalDate rentalDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;
    private Double lateFee;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rental rental = (Rental) o;
        return id != null && id.equals(rental.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}