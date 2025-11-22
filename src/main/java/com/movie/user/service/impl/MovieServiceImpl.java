package com.movie.user.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.movie.user.dto.ApiResponse;
import com.movie.user.dto.RatingRequest;
import com.movie.user.dto.CommentRequest;
import com.movie.user.entity.*;
import com.movie.user.repository.*;
import com.movie.user.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MovieServiceImpl implements MovieService {

    private static final String RECOMMENDATION_API_BASE = "http://127.0.0.1:5000";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public ResponseEntity<String> forwardMovieRequest(String path, String queryString) {
        // 原型模式：协同过滤推荐返回预设结果
        if (path.contains("collaborative")) {
            String prototypeResponse = "{\n" +
                "  \"user_id\": \"2\",\n" +
                "  \"count\": 10,\n" +
                "  \"recommendations\": [\n" +
                "    {\"movie_id\": 27205, \"title\": \"Inception\", \"predicted_rating\": 4.9, \"poster_path\": \"https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_QL75_UY281_CR5,0,190,281_.jpg\", \"imdb_url\": \"https://www.imdb.com/title/tt1375666/\"},\n" +
                "    {\"movie_id\": 60304, \"title\": \"The Avengers\", \"predicted_rating\": 4.8, \"poster_path\": \"https://m.media-amazon.com/images/M/MV5BNGE0YTVjNzUtNzJjOS00NGNlLTgxMzctZTY4YTE1Y2Y1ZTU4XkEyXkFqcGc@._V1_QL75_UX190_CR0,0,190,281_.jpg\", \"imdb_url\": \"https://www.imdb.com/title/tt0848228/\"},\n" +
                "    {\"movie_id\": 60308, \"title\": \"Man of Steel\", \"predicted_rating\": 4.7, \"poster_path\": \"https://m.media-amazon.com/images/M/MV5BMTk5ODk1NDkxMF5BMl5BanBnXkFtZTcwNTA5OTY0OQ@@._V1_QL75_UY281_CR0,0,190,281_.jpg\", \"imdb_url\": \"https://www.imdb.com/title/tt0770828/\"},\n" +
                "    {\"movie_id\": 76341, \"title\": \"Mad Max: Fury Road\", \"predicted_rating\": 4.6, \"poster_path\": \"https://m.media-amazon.com/images/M/MV5BN2EwM2I5OWMtMGQyMi00YzI1LTgzMTQtMzUxNDFhNTE3NGEwXkEyXkFqcGc@._V1_QL75_UY281_CR17,0,190,281_.jpg\", \"imdb_url\": \"https://www.imdb.com/title/tt1392190/\"},\n" +
                "    {\"movie_id\": 155, \"title\": \"The Dark Knight\", \"predicted_rating\": 4.9, \"poster_path\": \"https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_QL75_UY281_CR5,0,190,281_.jpg\", \"imdb_url\": \"https://www.imdb.com/title/tt0468569/\"},\n" +
                "    {\"movie_id\": 49026, \"title\": \"The Hobbit: An Unexpected Journey\", \"predicted_rating\": 4.5, \"poster_path\": \"https://m.media-amazon.com/images/M/MV5BMTYzNDE3OTQ3MF5BMl5BanBnXkFtZTgwODczMTg4MjE@._V1_QL75_UX190_CR0,0,190,281_.jpg\", \"imdb_url\": \"https://www.imdb.com/title/tt0903624/\"},\n" +
                "    {\"movie_id\": 122917, \"title\": \"The Hobbit: The Battle of the Five Armies\", \"predicted_rating\": 4.4, \"poster_path\": \"https://m.media-amazon.com/images/M/MV5BMTYzNDE3OTQ3MF5BMl5BanBnXkFtZTgwODczMTg4MjE@._V1_QL75_UX190_CR0,0,190,281_.jpg\", \"imdb_url\": \"https://www.imdb.com/title/tt2310332/\"},\n" +
                "    {\"movie_id\": 68721, \"title\": \"Pacific Rim\", \"predicted_rating\": 4.3, \"poster_path\": \"https://m.media-amazon.com/images/M/MV5BMTkzNzE1MTUwN15BMl5BanBnXkFtZTcwMjc0MjI0Mw@@._V1_QL75_UX190_CR0,0,190,281_.jpg\", \"imdb_url\": \"https://www.imdb.com/title/tt1663662/\"},\n" +
                "    {\"movie_id\": 70345, \"title\": \"Elysium\", \"predicted_rating\": 4.2, \"poster_path\": \"https://m.media-amazon.com/images/M/MV5BNDc2NjU0MTcwOV5BMl5BanBnXkFtZTcwMjE4MDY0Nw@@._V1_QL75_UY281_CR5,0,190,281_.jpg\", \"imdb_url\": \"https://www.imdb.com/title/tt1535108/\"},\n" +
                "    {\"movie_id\": 84136, \"title\": \"After Earth\", \"predicted_rating\": 4.0, \"poster_path\": \"https://m.media-amazon.com/images/M/MV5BNzk1MjQ0MzQxMF5BMl5BanBnXkFtZTcwNTI2MzcwNw@@._V1_QL75_UY281_CR0,0,190,281_.jpg\", \"imdb_url\": \"https://www.imdb.com/title/tt1815862/\"}\n" +
                "  ]\n" +
                "}";
            return ResponseEntity.ok(prototypeResponse);
        }

        try {
            String url = RECOMMENDATION_API_BASE + path;
            if (queryString != null && !queryString.isEmpty()) {
                url += "?" + queryString;
            }

            URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();
            RequestEntity<Void> request = new RequestEntity<>(HttpMethod.GET, uri);

            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            String responseBody = response.getBody();

            // Enrich the response with poster_path and imdb_url from local database
            if (responseBody != null) {
                responseBody = enrichResponseWithMovieMetadata(responseBody);
            }

            return ResponseEntity.status(response.getStatusCode()).body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"转发请求失败: " + e.getMessage() + "\"}");
        }
    }

    private String enrichResponseWithMovieMetadata(String responseBody) {
        try {
            // Parse JSON and add poster_path and imdb_url to movies
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);

            // Check if response has a 'recommendations' array
            if (rootNode.has("recommendations") && rootNode.get("recommendations").isArray()) {
                ArrayNode recommendations = (ArrayNode) rootNode.get("recommendations");
                for (JsonNode movieNode : recommendations) {
                    enrichMovieNode(movieNode);
                }
            }

            // Check if response has a 'movies' array
            if (rootNode.has("movies") && rootNode.get("movies").isArray()) {
                ArrayNode movies = (ArrayNode) rootNode.get("movies");
                for (JsonNode movieNode : movies) {
                    enrichMovieNode(movieNode);
                }
            }

            // Check if response has a 'results' array (for search API)
            if (rootNode.has("results") && rootNode.get("results").isArray()) {
                ArrayNode results = (ArrayNode) rootNode.get("results");
                for (JsonNode movieNode : results) {
                    enrichMovieNode(movieNode);
                }
            }

            // Check if response has a 'similar_movies' array (for knowledge graph API)
            if (rootNode.has("similar_movies") && rootNode.get("similar_movies").isArray()) {
                ArrayNode similarMovies = (ArrayNode) rootNode.get("similar_movies");
                for (JsonNode movieNode : similarMovies) {
                    enrichMovieNode(movieNode);
                }
            }

            return mapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            // If enrichment fails, return original response
            System.err.println("Failed to enrich response with metadata: " + e.getMessage());
            return responseBody;
        }
    }

    private void enrichMovieNode(JsonNode movieNode) {
        if (movieNode.has("title")) {
            String title = movieNode.get("title").asText();
            Optional<Movie> movieOpt = movieRepository.findFirstByTitleContainingIgnoreCase(title);
            if (movieOpt.isPresent()) {
                Movie movie = movieOpt.get();
                ObjectNode movieObject = (ObjectNode) movieNode;
                if (movie.getPosterPath() != null) {
                    movieObject.put("poster_path", movie.getPosterPath());
                }
                if (movie.getImdbUrl() != null) {
                    movieObject.put("imdb_url", movie.getImdbUrl());
                }
            }
        }
    }

    @Override
    public ApiResponse<Rating> rateMovie(Long userId, RatingRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }

        Optional<Movie> movieOpt = movieRepository.findFirstByTitleContainingIgnoreCase(request.getMovieTitle());
        if (movieOpt.isEmpty()) {
            return ApiResponse.error("电影不存在: " + request.getMovieTitle());
        }

        User user = userOpt.get();
        Movie movie = movieOpt.get();

        Optional<Rating> existingRatingOpt = ratingRepository.findByUserAndMovie(user, movie);

        Rating rating;
        if (existingRatingOpt.isPresent()) {
            rating = existingRatingOpt.get();
            rating.setScore(request.getScore());
        } else {
            rating = new Rating();
            rating.setUser(user);
            rating.setMovie(movie);
            rating.setScore(request.getScore());
        }

        Rating savedRating = ratingRepository.save(rating);
        return ApiResponse.success("评分成功", savedRating);
    }

    @Override
    public ApiResponse<Favorite> addToFavorites(Long userId, String movieTitle) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }

        Optional<Movie> movieOpt = movieRepository.findFirstByTitleContainingIgnoreCase(movieTitle);
        if (movieOpt.isEmpty()) {
            return ApiResponse.error("电影不存在: " + movieTitle);
        }

        User user = userOpt.get();
        Movie movie = movieOpt.get();

        if (favoriteRepository.existsByUserAndMovie(user, movie)) {
            return ApiResponse.error("电影已在收藏列表中");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setMovie(movie);

        Favorite savedFavorite = favoriteRepository.save(favorite);
        return ApiResponse.success("收藏成功", savedFavorite);
    }

    @Override
    public ApiResponse<String> removeFromFavorites(Long userId, String movieTitle) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }

        Optional<Movie> movieOpt = movieRepository.findFirstByTitleContainingIgnoreCase(movieTitle);
        if (movieOpt.isEmpty()) {
            return ApiResponse.error("电影不存在: " + movieTitle);
        }

        User user = userOpt.get();
        Movie movie = movieOpt.get();

        Optional<Favorite> favoriteOpt = favoriteRepository.findByUserAndMovie(user, movie);
        if (favoriteOpt.isEmpty()) {
            return ApiResponse.error("电影不在收藏列表中");
        }

        favoriteRepository.delete(favoriteOpt.get());
        return ApiResponse.success("取消收藏成功");
    }

    @Override
    public ApiResponse<List<Favorite>> getUserFavorites(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }

        List<Favorite> favorites = favoriteRepository.findByUser(userOpt.get());
        return ApiResponse.success(favorites);
    }

    @Override
    public ApiResponse<Comment> addComment(Long userId, CommentRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }

        Optional<Movie> movieOpt = movieRepository.findFirstByTitleContainingIgnoreCase(request.getMovieTitle());
        if (movieOpt.isEmpty()) {
            return ApiResponse.error("电影不存在: " + request.getMovieTitle());
        }

        User user = userOpt.get();
        Movie movie = movieOpt.get();

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setMovie(movie);
        comment.setContent(request.getContent());

        Comment savedComment = commentRepository.save(comment);
        return ApiResponse.success("评论成功", savedComment);
    }

    @Override
    public ApiResponse<List<Comment>> getMovieComments(String movieTitle) {
        Optional<Movie> movieOpt = movieRepository.findFirstByTitleContainingIgnoreCase(movieTitle);
        if (movieOpt.isEmpty()) {
            return ApiResponse.error("电影不存在: " + movieTitle);
        }

        List<Comment> comments = commentRepository.findByMovie(movieOpt.get());
        return ApiResponse.success(comments);
    }

    @Override
    public ApiResponse<List<Rating>> getUserRatings(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }

        List<Rating> ratings = ratingRepository.findByUser(userOpt.get());
        return ApiResponse.success(ratings);
    }
}

