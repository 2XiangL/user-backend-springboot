package com.movie.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.Data;

@Data
public class RatingRequest {
    @NotBlank
    private String movieTitle;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double score;
}

