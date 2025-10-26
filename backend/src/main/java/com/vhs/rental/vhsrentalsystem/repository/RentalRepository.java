package com.vhs.rental.vhsrentalsystem.repository;

import com.vhs.rental.vhsrentalsystem.model.Rental;
import com.vhs.rental.vhsrentalsystem.model.User;
import com.vhs.rental.vhsrentalsystem.model.Vhs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Spring Data JPA repository for the Rental entity.
 * Provides CRUD operations and custom queries.
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    Optional<Rental> findByVhsAndReturnDateIsNull(Vhs vhs); //find active rental by vhs

    boolean existsByUserAndReturnDateIsNull(User user); //check if user has any active rentals

    boolean existsByVhsAndReturnDateIsNull(Vhs vhs); //check if vhs is currently rented

    List<Rental> findByUser(User user); //get all rentals for a user (history)
}
