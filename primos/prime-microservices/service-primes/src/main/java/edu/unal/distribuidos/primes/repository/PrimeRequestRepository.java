package edu.unal.distribuidos.primes.repository;

import edu.unal.distribuidos.primes.model.PrimeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrimeRequestRepository extends JpaRepository<PrimeRequest, String> {
}
