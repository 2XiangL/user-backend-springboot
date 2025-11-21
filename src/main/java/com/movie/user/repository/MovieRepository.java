package com.movie.user.repository;

import com.movie.user.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByMovieId(Long movieId);

    List<Movie> findByTitleContainingIgnoreCase(String title);

    Optional<Movie> findFirstByTitleContainingIgnoreCase(String title);

    boolean existsByMovieId(Long movieId);

    boolean existsByTitleContainingIgnoreCase(String title);
}

