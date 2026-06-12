package com.marketplace.product_service.service;

import com.marketplace.product_service.dto.ProductoDto;
import com.marketplace.product_service.exception.RecursoNoEncontradoException;
import com.marketplace.product_service.model.Producto;
import com.marketplace.product_service.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repo;
    private final WebClient webClient;

    public List<Producto> listarTodos() {
        return repo.findAll();
    }

    public Producto buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));
    }

    public Producto crear(ProductoDto dto) {
        Producto producto = Producto.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .stock(dto.getStock())
                .active(true)
                .build();

        Producto guardado = repo.save(producto);

        try {
            webClient.post()
                    .uri("/sync")
                    .bodyValue(guardado)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe();
        } catch (Exception e) {
            System.err.println("Error de sincronización: " + e.getMessage());
        }

        return guardado;
    }

    public Producto actualizar(Long id, ProductoDto dto) {
        Producto existente = buscarPorId(id);
        existente.setName(dto.getName());
        existente.setPrice(dto.getPrice());
        existente.setDescription(dto.getDescription());
        return repo.save(existente);
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("No se puede eliminar, ID no existe");
        }
        repo.deleteById(id);
    }

    public ProductoService(ProductoRepository repo, WebClient.Builder webClientBuilder) {
        this.repo = repo;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8089/api/search").build();
    }
}