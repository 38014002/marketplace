CREATE TABLE users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL
);

-- Insertar un usuario administrador por defecto (la contraseña es 'admin123' encriptada)
INSERT INTO users (username, password, email, role) 
VALUES ('admin', '$2a$10$Xptf7pT.9XWp.E1Z.x5.DeS1V1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z', 'admin@marketplace.com', 'ADMIN');