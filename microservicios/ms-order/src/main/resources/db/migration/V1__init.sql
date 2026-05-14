CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_product_ids (
    order_id BIGINT NOT NULL,
    product_ids BIGINT NOT NULL,
    CONSTRAINT fk_order_ref FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

INSERT INTO orders (user_id, total_amount, status) VALUES (1, 500.0, 'PENDING');
INSERT INTO order_product_ids (order_id, product_ids) VALUES (1, 101);