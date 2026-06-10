package edu.unal.distribuidos.worker.repository;

import edu.unal.distribuidos.worker.entity.Prime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PrimeRepository extends JpaRepository<Prime, Long> {
  boolean existsByNumber(String number);
  
  List<Prime> findByRequestId(String requestId);
  
  @Query("SELECT COUNT(p) FROM Prime p WHERE p.requestId = :requestId")
  int countByRequestId(String requestId);
}
