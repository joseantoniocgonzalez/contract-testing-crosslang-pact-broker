package com.example.provider.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class OrdersController {

  private static final AtomicInteger ID_SEQ = new AtomicInteger(1);

  @PostMapping(
      path = "/orders",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<OrderResponse> createOrder(
      @RequestHeader("Authorization") String authorization,
      @Valid @RequestBody CreateOrderRequest request
  ) {
    // Token simple (no OAuth/JWT)
    if (authorization == null || !authorization.equals("Bearer token-abc123")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    int orderId = ID_SEQ.getAndIncrement();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new OrderResponse(orderId, "CREATED"));
  }

  public record CreateOrderRequest(@Min(1) int productId, @Min(1) int quantity) {}
  public record OrderResponse(int orderId, String status) {}
}
