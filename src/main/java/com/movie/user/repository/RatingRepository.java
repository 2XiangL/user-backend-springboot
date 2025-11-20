package com.movie.user.repository;

import com.movie.user.entity.Rating;
import com.movie.user.entity.User;
import com.movie.user.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndMovie(User user, Movie movie);

    List<Rating> findByUser(User user);

    List<Rating> findByMovie(Movie movie);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.movie = :movie")
    Double findAverageScoreByMovie(@Param("movie") Movie movie);

    @Query("SELECT r FROM Rating r WHERE r.user.id = :userId ORDER BY r.score DESC")
    List<Rating> findTopRatingsByUser(@Param("userId") Long userId);

    boolean existsByUserAndMovie(User user, Movie movie);
}

