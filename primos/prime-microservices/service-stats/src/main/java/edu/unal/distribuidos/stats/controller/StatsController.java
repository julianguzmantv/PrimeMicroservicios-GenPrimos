package edu.unal.distribuidos.stats.controller;

import edu.unal.distribuidos.stats.repository.PrimeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class StatsController {
  private final PrimeRepository primeRepository;
  
  public StatsController(PrimeRepository primeRepository) {
    this.primeRepository = primeRepository;
  }
  
  @GetMapping("/stats/basic")
  public Map<String, Object> basic(@RequestParam(value = "digitos", required = false, defaultValue = "2") int digitos) {
    long total = primeRepository.totalCount();
    long count = primeRepository.countByDigits(digitos);
    long percentage = (count > 0 && total > 0) ? Math.round(count * 100.0 / total) : 0;
    return Map.of(
      "cant", count,
      "digitos", digitos,
      "total", total,
      "accion", percentage
    );
  }
}
