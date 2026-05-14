package com.marketplace.ms_search.ms_search.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.marketplace.ms_search.ms_search.dto.ProductResponseDto;
import com.marketplace.ms_search.ms_search.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService service;
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>>
    buscar(@RequestParam String query) {

    return ResponseEntity.ok(service.buscar(query));
    }
}