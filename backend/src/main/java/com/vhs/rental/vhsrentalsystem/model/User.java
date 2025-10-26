package com.vhs.rental.vhsrentalsystem.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Represents a User entity in the system.
 * This class is mapped to the 'users' table in the database.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    @Column(nullable = false,  unique = true)
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Rental> rentals = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
