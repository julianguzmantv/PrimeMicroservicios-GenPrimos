package edu.unal.distribuidos.worker.listener;

import edu.unal.distribuidos.common.dto.PrimeTask;
import edu.unal.distribuidos.worker.service.PrimeService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PrimeTaskListener {
  private final PrimeService primeService;
  private final JdbcTemplate jdbcTemplate;

  public PrimeTaskListener(PrimeService primeService, JdbcTemplate jdbcTemplate) {
    this.primeService = primeService;
    this.jdbcTemplate = jdbcTemplate;
  }

  @RabbitListener(queues = "${app.queue.primeTasks}")
  public void handle(PrimeTask task) {
    try {
      System.out.println("Processing task: " + task.getRequestId() + 
                         " - cantidad: " + task.getCantidad() + 
                         ", digitos: " + task.getDigitos());
      
      // Update request status to IN_PROGRESS
      updateRequestStatus(task.getRequestId(), "IN_PROGRESS");
      
      // Generate primes
      List<String> primes = primeService.generatePrimes(
        task.getRequestId(), 
        task.getCantidad(), 
        task.getDigitos()
      );
      
      // Update request with generated count and status
      updateRequestProgress(task.getRequestId(), primes.size(), "COMPLETED");
      
      System.out.println("Completed task: " + task.getRequestId() + 
                         " - generated " + primes.size() + " primes");
    } catch (Exception e) {
      System.err.println("Error processing task " + task.getRequestId() + ": " + e.getMessage());
      e.printStackTrace();
      updateRequestStatus(task.getRequestId(), "FAILED");
    }
  }

  private void updateRequestStatus(String requestId, String status) {
    jdbcTemplate.update(
      "UPDATE prime_requests SET estado = ? WHERE id = ?",
      status, requestId
    );
  }

  private void updateRequestProgress(String requestId, int generated, String status) {
    jdbcTemplate.update(
      "UPDATE prime_requests SET generados = ?, estado = ? WHERE id = ?",
      generated, status, requestId
    );
  }
}
