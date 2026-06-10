package edu.unal.distribuidos.common.dto;

import java.time.LocalDateTime;

public class PrimeRequestDto {
    private String id;
    private int cantidad;
    private int digitos;
    private String estado; // PENDING, IN_PROGRESS, COMPLETED
    private int generados;
    private LocalDateTime createdAt;

    public PrimeRequestDto() {}

    public PrimeRequestDto(String id, int cantidad, int digitos, String estado, int generados, LocalDateTime createdAt) {
        this.id = id;
        this.cantidad = cantidad;
        this.digitos = digitos;
        this.estado = estado;
        this.generados = generados;
        this.createdAt = createdAt;
    }

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
