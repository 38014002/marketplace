package com.marketplace.ms_review.ms_review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDTO {
    @NotNull
    private Long productId;

    @NotNull
    private Long userId;

    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank
    private String comment;
}
