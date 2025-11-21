package com.movie.user.repository;

import com.movie.user.entity.Comment;
import com.movie.user.entity.User;
import com.movie.user.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUser(User user);

    List<Comment> findByMovie(Movie movie);

    @Query("SELECT c FROM Comment c WHERE c.movie.movieId = :movieId ORDER BY c.createdAt DESC")
    List<Comment> findByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByUserId(@Param("userId") Long userId);
}

