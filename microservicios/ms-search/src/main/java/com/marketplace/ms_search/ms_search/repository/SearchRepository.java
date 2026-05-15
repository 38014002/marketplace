package com.marketplace.ms_search.ms_search.repository;

import com.marketplace.ms_search.ms_search.model.SearchProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<SearchProduct, Long> {

    List<SearchProduct> findByNameContainingIgnoreCase(String query);
}