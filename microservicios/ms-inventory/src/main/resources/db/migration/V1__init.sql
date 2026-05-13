CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL UNIQUE,
    stock INT NOT NULL DEFAULT 0
);

-- Insertar datos de prueba
INSERT INTO inventory (product_id, stock) VALUES (1, 100);
INSERT INTO inventory (product_id, stock) VALUES (2, 50);
INSERT INTO inventory (product_id, stock) VALUES (3, 0);