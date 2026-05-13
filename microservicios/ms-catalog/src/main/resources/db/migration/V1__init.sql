-- Crear la tabla de productos para el catálogo
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar algunos productos de prueba iniciales
INSERT INTO products (name, description, price, category) VALUES 
('Laptop Pro 14', 'Laptop potente para desarrollo y diseño', 1299.99, 'ELECTRONICS'),
('Mouse Inalámbrico', 'Mouse ergonómico con conexión 2.4GHz', 25.50, 'ACCESSORIES'),
('Teclado Mecánico RGB', 'Teclado con switches red y retroiluminación', 89.00, 'ACCESSORIES'),
('Monitor 27 Pulgadas', 'Monitor 4K ideal para gaming y oficina', 350.00, 'ELECTRONICS');