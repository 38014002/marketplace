-- Crear tabla productos
CREATE TABLE IF NOT EXISTS pagos (
  id_pago INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  precio DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL
);

-- Insertar datos de prueba
INSERT INTO pagos (nombre, precio, stock) VALUES
('Laptop Gamer', 1200.00, 10),
('Mouse Inalámbrico', 25.50, 50),
('Teclado Mecánico', 75.00, 30);
