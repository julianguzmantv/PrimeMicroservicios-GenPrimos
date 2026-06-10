package edu.unal.distribuidos.stats.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "primes")
public class Prime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false, unique = true)
  private Long number;
  
  @Column(nullable = false)
  private Integer digits;

  public Long getId() { return id; }
  public Long getNumber() { return number; }
  public Integer getDigits() { return digits; }
}
