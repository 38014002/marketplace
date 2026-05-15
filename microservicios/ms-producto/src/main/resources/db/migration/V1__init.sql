-- Crear tabla productos
CREATE TABLE IF NOT EXISTS productos (
  id_producto INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  precio DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL,
  categoria VARCHAR(100),
  activo BOOLEAN;
);

-- Insertar datos de prueba
INSERT INTO productos (nombre, precio, stock, categoria, activo) VALUES
('Laptop Gamer', 1200.00, 10, 'Electrónica', true),
('Smartphone', 800.00, 20, 'Electrónica', true),
('Mouse Inalámbrico', 25.50, 50, 'Electrónica', true),
('Teclado Mecánico', 75.00, 30, 'Electrónica', true );
