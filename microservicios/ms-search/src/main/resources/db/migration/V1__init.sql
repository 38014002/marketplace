CREATE TABLE search_products (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    price DOUBLE,
    category VARCHAR(100)
);

INSERT INTO search_products (id, name, description, price, category) VALUES
(1, 'Laptop Pro 14', 'Laptop potente para desarrollo y diseño', 1299.99, 'ELECTRONICS'),
(2, 'Mouse Inalámbrico', 'Mouse ergonómico con conexión 2.4GHz', 25.50, 'ACCESSORIES'),
(3, 'Teclado Mecánico RGB', 'Teclado con switches red y retroiluminación', 89.00, 'ACCESSORIES');
