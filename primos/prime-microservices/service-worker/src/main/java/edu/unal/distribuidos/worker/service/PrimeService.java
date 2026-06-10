package edu.unal.distribuidos.worker.service;

import edu.unal.distribuidos.worker.entity.Prime;
import edu.unal.distribuidos.worker.repository.PrimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrimeService {
  private final PrimeRepository primeRepository;
  
  public PrimeService(PrimeRepository primeRepository) {
    this.primeRepository = primeRepository;
  }
  
  /**
   * Generate prime numbers for a specific request
   * @param requestId Unique request identifier
   * @param cantidad Number of primes to generate
   * @param digitos Number of digits for each prime
   * @return List of generated prime numbers as strings
   */
  @Transactional
  public List<String> generatePrimes(String requestId, int cantidad, int digitos) {
    List<String> primes = new ArrayList<>();
    
    for (int i = 0; i < cantidad; i++) {
      BigInteger prime = MillerRabin.generatePrime(digitos);
      String primeStr = prime.toString();
      
      // Check if already exists to avoid duplicates
      if (!primeRepository.existsByNumber(primeStr)) {
        primeRepository.save(new Prime(primeStr, digitos, requestId));
        primes.add(primeStr);
      } else {
        // If duplicate, try again
        i--;
      }
    }
    
    return primes;
  }
}
