package edu.unal.distribuidos.stats.repository;

import edu.unal.distribuidos.stats.entity.Prime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrimeRepository extends JpaRepository<Prime, Long> {
  @Query("SELECT COUNT(p) FROM Prime p WHERE p.digits = :digits")
  long countByDigits(@Param("digits") int digits);
  
  @Query("SELECT COUNT(p) FROM Prime p")
  long totalCount();
}
