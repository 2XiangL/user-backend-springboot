package com.movie.user.controller;

import com.movie.user.dto.ApiResponse;
import com.movie.user.dto.RatingRequest;
import com.movie.user.dto.CommentRequest;
import com.movie.user.entity.*;
import com.movie.user.service.MovieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/**")
    public ResponseEntity<String> forwardMovieRequest(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "n", required = false) String n,
            @RequestParam(value = "movie", required = false) String movie,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "user_id", required = false) String userId,
            @RequestParam(value = "fuzzy", required = false) String fuzzy,
            @RequestParam(value = "keywords", required = false) String keywords,
            HttpServletRequest request
    ) {
        StringBuilder queryString = new StringBuilder();

        if (q != null) queryString.append("q=").append(q).append("&");
        if (n != null) queryString.append("n=").append(n).append("&");
        if (movie != null) queryString.append("movie=").append(movie).append("&");
        if (type != null) queryString.append("type=").append(type).append("&");
        if (keyword != null) queryString.append("keyword=").append(keyword).append("&");
        if (userId != null) queryString.append("user_id=").append(userId).append("&");
        if (fuzzy != null) queryString.append("fuzzy=").append(fuzzy).append("&");
        if (keywords != null) queryString.append("keywords=").append(keywords).append("&");

        String requestUri = request.getRequestURI();
        String path = requestUri.replaceFirst("/api/movies", "/api");
        String qs = queryString.toString();
        if (!qs.isEmpty()) {
            qs = qs.substring(0, qs.length() - 1);
        }

        return movieService.forwardMovieRequest(path, qs);
    }

    @PostMapping("/{userId}/rating")
    public ApiResponse<Rating> rateMovie(
            @PathVariable Long userId,
            @Valid @RequestBody RatingRequest request) {
        return movieService.rateMovie(userId, request);
    }

    @PostMapping("/{userId}/favorites")
    public ApiResponse<Favorite> addToFavorites(
            @PathVariable Long userId,
            @RequestParam String movieTitle) {
        return movieService.addToFavorites(userId, movieTitle);
    }

    @DeleteMapping("/{userId}/favorites")
    public ApiResponse<String> removeFromFavorites(
            @PathVariable Long userId,
            @RequestParam String movieTitle) {
        return movieService.removeFromFavorites(userId, movieTitle);
    }

    @GetMapping("/{userId}/favorites")
    public ApiResponse<List<Favorite>> getUserFavorites(@PathVariable Long userId) {
        return movieService.getUserFavorites(userId);
    }

    @PostMapping("/{userId}/comments")
    public ApiResponse<Comment> addComment(
            @PathVariable Long userId,
            @Valid @RequestBody CommentRequest request) {
        return movieService.addComment(userId, request);
    }

    @GetMapping("/comments")
    public ApiResponse<List<Comment>> getMovieComments(@RequestParam String movieTitle) {
        return movieService.getMovieComments(movieTitle);
    }

    @GetMapping("/{userId}/ratings")
    public ApiResponse<List<Rating>> getUserRatings(@PathVariable Long userId) {
        return movieService.getUserRatings(userId);
    }
}

