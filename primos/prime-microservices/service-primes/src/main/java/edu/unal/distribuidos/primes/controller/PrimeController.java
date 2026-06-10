package edu.unal.distribuidos.primes.controller;

import edu.unal.distribuidos.common.dto.PrimeTask;
import edu.unal.distribuidos.common.dto.PrimeRequestDto;
import edu.unal.distribuidos.primes.model.PrimeRequest;
import edu.unal.distribuidos.primes.repository.PrimeRequestRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/primes")
public class PrimeController {
  private final RabbitTemplate rabbitTemplate;
  private final PrimeRequestRepository requestRepository;
  private final JdbcTemplate jdbcTemplate;
  private final String queueName;

  public PrimeController(RabbitTemplate rabbitTemplate, 
                        PrimeRequestRepository requestRepository,
                        JdbcTemplate jdbcTemplate,
                        @Value("${app.queue.primeTasks}") String queueName) {
    this.rabbitTemplate = rabbitTemplate;
    this.requestRepository = requestRepository;
    this.jdbcTemplate = jdbcTemplate;
    this.queueName = queueName;
  }

  /**
   * Endpoint: New
   * Permite solicitar la generación de nuevos números primos
   */
  @PostMapping("/new")
  public ResponseEntity<Map<String, String>> createPrimeRequest(
      @RequestParam("cantidad") int cantidad,
      @RequestParam("digitos") int digitos) {
    try {
      // Generate unique ID
      String requestId = UUID.randomUUID().toString();
      
      // Persist request in DB
      PrimeRequest request = new PrimeRequest(requestId, cantidad, digitos);
      requestRepository.save(request);
      
      // Queue task for workers
      PrimeTask task = new PrimeTask(requestId, cantidad, digitos);
      rabbitTemplate.convertAndSend(queueName, task);
      
      Map<String, String> response = new HashMap<>();
      response.put("id", requestId);
      response.put("message", "Request queued successfully");
      
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      e.printStackTrace();
      Map<String, String> error = new HashMap<>();
      error.put("error", "Failed to create request: " + e.getMessage());
      return ResponseEntity.internalServerError().body(error);
    }
  }

  /**
   * Endpoint: Status
   * Permite saber del estado de la solicitud
   */
  @GetMapping("/status/{id}")
  public ResponseEntity<?> getRequestStatus(@PathVariable("id") String id) {
    Optional<PrimeRequest> requestOpt = requestRepository.findById(id);
    
    if (requestOpt.isEmpty()) {
      Map<String, String> error = new HashMap<>();
      error.put("error", "Request not found");
      return ResponseEntity.notFound().build();
    }
    
    PrimeRequest request = requestOpt.get();
    Map<String, Object> response = new HashMap<>();
    response.put("id", request.getId());
    response.put("cantidad", request.getCantidad());
    response.put("generados", request.getGenerados());
    response.put("estado", request.getEstado());
    response.put("digitos", request.getDigitos());
    
    return ResponseEntity.ok(response);
  }

  /**
   * Endpoint: Result
   * Permite conocer los números primos generados
   */
  @GetMapping("/result/{id}")
  public ResponseEntity<?> getRequestResult(@PathVariable("id") String id) {
    Optional<PrimeRequest> requestOpt = requestRepository.findById(id);
    
    if (requestOpt.isEmpty()) {
      Map<String, String> error = new HashMap<>();
      error.put("error", "Request not found");
      return ResponseEntity.notFound().build();
    }
    
    PrimeRequest request = requestOpt.get();
    
    // Fetch generated primes from database
    List<String> primes = jdbcTemplate.queryForList(
      "SELECT number FROM primes WHERE request_id = ? ORDER BY id",
      String.class,
      id
    );
    
    Map<String, Object> response = new HashMap<>();
    response.put("id", request.getId());
    response.put("cantidad", request.getCantidad());
    response.put("generados", request.getGenerados());
    response.put("estado", request.getEstado());
    response.put("digitos", request.getDigitos());
    response.put("primes", primes);
    
    return ResponseEntity.ok(response);
  }

  @GetMapping("/health")
  public String health() {
    return "OK";
  }
}
