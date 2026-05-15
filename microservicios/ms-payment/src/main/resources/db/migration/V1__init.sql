-- Crear la tabla de pagos
CREATE TABLE IF NOT EXISTS pagos (
  id LONG AUTO_INCREMENT PRIMARY KEY,
  orderId LONG NOT NULL,
  amount BIGDECIMAL(10,2) NOT NULL,
  paymentMethod VARCHAR(50) NOT NULL,
  status VARCHAR(20) NOT NULL
);

-- Insertar datos de prueba
INSERT INTO pagos (orderId, amount, paymentMethod, status) VALUES
(1, 1200.00, 'Tarjeta de Crédito', 'COMPLETADO'),
(2, 25.50, 'PayPal', 'PENDIENTE'),
(3, 75.00, 'Transferencia Bancaria', 'RECHAZADO');
