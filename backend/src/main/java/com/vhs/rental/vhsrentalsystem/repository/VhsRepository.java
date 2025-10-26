package com.vhs.rental.vhsrentalsystem.repository;

import com.vhs.rental.vhsrentalsystem.model.Vhs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Spring Data JPA repository for the Vhs entity.
 * Provides CRUD operations and custom queries for filtering and searching.
 */
@Repository
public interface VhsRepository extends JpaRepository<Vhs, Long> {
    List<Vhs> findByGenre(String genre);

    List<Vhs> findByReleaseYear(int releaseYear);

    List<Vhs> findByGenreAndReleaseYear(String genre, int releaseYear);

    List<Vhs> findLatestTenByOrderByReleaseYearDesc();
}
