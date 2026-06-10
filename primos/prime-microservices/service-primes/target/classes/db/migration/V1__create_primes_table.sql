-- Create primes table
CREATE TABLE IF NOT EXISTS primes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  number VARCHAR(50) NOT NULL UNIQUE,
  digits INT NOT NULL,
  request_id VARCHAR(36),
  INDEX idx_digits (digits),
  INDEX idx_request_id (request_id)
);
