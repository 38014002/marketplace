package com.marketplace.product_service.service;

import com.marketplace.product_service.dto.ProductoDto;
import com.marketplace.product_service.exception.RecursoNoEncontradoException;
import com.marketplace.product_service.model.Producto;
import com.marketplace.product_service.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public List<Producto> listarTodos() {
        return repo.findAll();
    }

    public Producto buscarPorId(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
    }

    public Producto crear(ProductoDto dto) {
        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .precio(dto.getPrecio())
                .build();
        return repo.save(producto);
    }

    public Producto actualizar(Integer id, ProductoDto dto) {
        Producto existente = buscarPorId(id);
        existente.setNombre(dto.getNombre());
        existente.setPrecio(dto.getPrecio());
        return repo.save(existente);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}