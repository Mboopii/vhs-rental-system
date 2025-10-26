package com.vhs.rental.vhsrentalsystem.repository;

import com.vhs.rental.vhsrentalsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * Spring Data JPA repository for the User entity.
 * Provides CRUD operations and custom queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); //used for duplicate check
}
