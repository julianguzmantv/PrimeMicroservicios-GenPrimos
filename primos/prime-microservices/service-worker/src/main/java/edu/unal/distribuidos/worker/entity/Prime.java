package edu.unal.distribuidos.worker.entity;

import jakarta.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "primes")
public class Prime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false, unique = true, length = 50)
  private String number;
  
  @Column(nullable = false)
  private Integer digits;

  @Column(name = "request_id", length = 36)
  private String requestId;

  public Prime() {}
  public Prime(String number, Integer digits, String requestId) {
    this.number = number;
    this.digits = digits;
    this.requestId = requestId;
  }
  
  public Long getId() { return id; }
  public String getNumber() { return number; }
  public void setNumber(String number) {
    this.number = number;
    this.digits = number.length();
  }
  public Integer getDigits() { return digits; }
  public String getRequestId() { return requestId; }
  public void setRequestId(String requestId) { this.requestId = requestId; }
}
