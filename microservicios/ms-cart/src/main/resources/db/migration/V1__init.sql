-- Crear la tabla de carrito
CREATE TABLE carrito (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId LONG NOT NULL,
    productId LONG NOT NULL,
    quantity INT NOT NULL
);

-- Insertar algunos datos de prueba iniciales
INSERT INTO carrito (userId, productId, quantity) VALUES 
(1, 1, 1),
(1, 2, 2);