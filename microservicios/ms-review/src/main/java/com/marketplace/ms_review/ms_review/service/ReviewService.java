package com.marketplace.ms_review.ms_review.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.marketplace.ms_review.ms_review.dto.ReviewDTO;
import com.marketplace.ms_review.ms_review.exception.ReviewNotFoundException;
import com.marketplace.ms_review.ms_review.model.Review;
import com.marketplace.ms_review.ms_review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository repository;

    // LISTAR TODOS
    public List<Review> listarTodos() {
        log.info("Listando todas las reviews");
        return repository.findAll();
    }

    // BUSCAR POR ID
    public Review buscarPorId(Long id) {
        log.info("Buscando review con id {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Review no encontrada {}", id);
                    return new ReviewNotFoundException(id);
                });
    }

    // CREAR REVIEW
    public Review crear(ReviewDTO dto) {

        log.info("Creando review para producto {}",
                dto.getProductId());

        Review review = Review.builder()
                .productId(dto.getProductId())
                .userId(dto.getUserId())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        Review saved = repository.save(review);

        log.info("Review creada con id {}", saved.getId());

        return saved;
    }

    // ACTUALIZAR REVIEW
    public Review actualizar(Long id, ReviewDTO dto) {

        log.info("Actualizando review {}", id);

        Review review = buscarPorId(id);

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review updated = repository.save(review);

        log.info("Review actualizada {}", updated.getId());

        return updated;
    }

    // ELIMINAR REVIEW
    public void eliminar(Long id) {

        log.info("Eliminando review {}", id);

        Review review = buscarPorId(id);

        repository.delete(review);

        log.info("Review eliminada {}", id);
    }

    // BUSCAR REVIEWS POR PRODUCTO
    public List<Review> buscarPorProducto(Long productId) {

        log.info("Buscando reviews del producto {}",
                productId);

        return repository.findByProductId(productId);
    }
}