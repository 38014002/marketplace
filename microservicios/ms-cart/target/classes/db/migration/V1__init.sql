CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL
);

INSERT INTO cart_items (user_id, product_id, quantity) VALUES
(1, 1, 1),
(1, 2, 2);
