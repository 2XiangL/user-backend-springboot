package com.movie.user.service;

import com.movie.user.dto.ApiResponse;
import com.movie.user.dto.RatingRequest;
import com.movie.user.dto.CommentRequest;
import com.movie.user.entity.Rating;
import com.movie.user.entity.Favorite;
import com.movie.user.entity.Comment;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MovieService {
    ResponseEntity<String> forwardMovieRequest(String path, String queryString);

    ApiResponse<Rating> rateMovie(Long userId, RatingRequest request);

    ApiResponse<Favorite> addToFavorites(Long userId, String movieTitle);

    ApiResponse<String> removeFromFavorites(Long userId, String movieTitle);

    ApiResponse<List<Favorite>> getUserFavorites(Long userId);

    ApiResponse<Comment> addComment(Long userId, CommentRequest request);

    ApiResponse<List<Comment>> getMovieComments(String movieTitle);

    ApiResponse<List<Rating>> getUserRatings(Long userId);
}

