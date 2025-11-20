package com.movie.user.repository;

import com.movie.user.entity.Favorite;
import com.movie.user.entity.User;
import com.movie.user.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndMovie(User user, Movie movie);

    List<Favorite> findByUser(User user);

    List<Favorite> findByMovie(Movie movie);

    boolean existsByUserAndMovie(User user, Movie movie);
}

