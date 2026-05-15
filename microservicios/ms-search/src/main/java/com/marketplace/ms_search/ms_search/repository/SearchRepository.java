package com.marketplace.ms_search.ms_search.repository;

import com.marketplace.ms_search.ms_search.model.SearchProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<SearchProduct, Long> {

    // Búsqueda simple por nombre
    List<SearchProduct> findByNombreContainingIgnoreCase(String nombre);

    // Búsqueda "Pro": busca en nombre o categoría
    @Query("SELECT s FROM SearchProduct s WHERE " +
            "LOWER(s.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.categoria) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<SearchProduct> buscarFlexible(@Param("query") String query);
}