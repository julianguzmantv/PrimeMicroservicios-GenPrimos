-- Create prime_requests table
CREATE TABLE prime_requests (
    id VARCHAR(36) PRIMARY KEY,
    cantidad INT NOT NULL,
    digitos INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    generados INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_estado (estado),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
