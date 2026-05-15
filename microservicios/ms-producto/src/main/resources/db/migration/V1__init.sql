-- Borramos rastro de intentos anteriores
DROP TABLE IF EXISTS products;

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(100) NOT NULL
);

-- Insertamos datos que SI coinciden con tu modelo Java
INSERT INTO products (name, description, price, category) VALUES
('Laptop Gamer', 'Potente laptop con RTX 4060', 1200.00, 'Computación'),
('Mouse RGB', 'Mouse óptico 12000 DPI', 45.50, 'Accesorios');