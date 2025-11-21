package com.movie.user.service;

import com.movie.user.entity.Movie;
import com.movie.user.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Order(1)
public class DataInitService implements CommandLineRunner {

    @Autowired
    private MovieRepository movieRepository;

    private static final String CSV_FILE_PATH = "./data/imdblink_img_data.csv";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("开始从CSV加载电影海报和IMDB链接数据...");

        Map<String, MovieData> movieDataMap = loadMovieDataFromCSV();

        int updatedCount = 0;
        for (MovieData data : movieDataMap.values()) {
            Optional<Movie> movieOpt = movieRepository.findFirstByTitleContainingIgnoreCase(data.title);
            if (movieOpt.isPresent()) {
                Movie movie = movieOpt.get();
                movie.setPosterPath(data.imgUrl);
                movie.setImdbUrl(data.imdbUrl);
                movieRepository.save(movie);
                updatedCount++;
            }
        }

        System.out.println("数据加载完成！更新了 " + updatedCount + " 部电影的海报和IMDB链接信息。");
    }

    private Map<String, MovieData> loadMovieDataFromCSV() {
        Map<String, MovieData> movieDataMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);
                if (fields.length >= 3) {
                    String title = fields[0].trim();
                    String imdbUrl = fields[1].trim();
                    String imgUrl = fields[2].trim();

                    movieDataMap.put(title.toLowerCase(), new MovieData(title, imdbUrl, imgUrl));
                }
            }

        } catch (IOException e) {
            System.err.println("读取CSV文件时出错: " + e.getMessage());
        }

        return movieDataMap;
    }

    private String[] parseCsvLine(String line) {
        // Simple CSV parser that handles quoted fields with commas
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    private static class MovieData {
        String title;
        String imdbUrl;
        String imgUrl;

        MovieData(String title, String imdbUrl, String imgUrl) {
            this.title = title;
            this.imdbUrl = imdbUrl;
            this.imgUrl = imgUrl;
        }
    }
}

