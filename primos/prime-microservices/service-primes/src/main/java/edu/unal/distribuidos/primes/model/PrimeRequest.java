package edu.unal.distribuidos.primes.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prime_requests")
public class PrimeRequest {
    @Id
    private String id;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private int digitos;

    @Column(nullable = false)
    private String estado; // PENDING, IN_PROGRESS, COMPLETED

    @Column(nullable = false)
    private int generados = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public PrimeRequest() {}

    public PrimeRequest(String id, int cantidad, int digitos) {
        this.id = id;
        this.cantidad = cantidad;
        this.digitos = digitos;
        this.estado = "PENDING";
        this.generados = 0;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public int getDigitos() { return digitos; }
    public void setDigitos(int digitos) { this.digitos = digitos; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getGenerados() { return generados; }
    public void setGenerados(int generados) { this.generados = generados; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
