package edu.unal.distribuidos.common.dto;

public class PrimeTask {
    private String requestId;
    private int cantidad;
    private int digitos;

    public PrimeTask() {}
    public PrimeTask(String requestId, int cantidad, int digitos) {
        this.requestId = requestId;
        this.cantidad = cantidad;
        this.digitos = digitos;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public int getDigitos() { return digitos; }
    public void setDigitos(int digitos) { this.digitos = digitos; }
}
