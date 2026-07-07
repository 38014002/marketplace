CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL
);

INSERT INTO payments (order_id, amount, payment_method, status) VALUES
(1, 1200.00, 'Tarjeta de Credito', 'COMPLETADO'),
(2, 25.50, 'PayPal', 'PENDIENTE'),
(3, 75.00, 'Transferencia Bancaria', 'RECHAZADO');
