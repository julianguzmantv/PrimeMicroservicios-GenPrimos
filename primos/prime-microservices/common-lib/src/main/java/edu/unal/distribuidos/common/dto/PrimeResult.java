package edu.unal.distribuidos.common.dto;

public class PrimeResult {
    private long number;
    private int digits;

    public PrimeResult() {}
    public PrimeResult(long number) { this.number = number; this.digits = Long.toString(number).length(); }
    public long getNumber() { return number; }
    public void setNumber(long number) { this.number = number; this.digits = Long.toString(number).length(); }
    public int getDigits() { return digits; }
}
