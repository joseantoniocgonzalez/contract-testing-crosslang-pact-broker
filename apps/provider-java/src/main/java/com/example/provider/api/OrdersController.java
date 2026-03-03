package com.example.provider.api;

import com.example.provider.orders.OrderEntity;
import com.example.provider.orders.OrderRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrdersController {

  private final OrderRepository orders;

  public OrdersController(OrderRepository orders) {
    this.orders = orders;
  }

  @PostMapping(
      path = "/orders",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<OrderResponse> createOrder(
      @RequestHeader("Authorization") String authorization,
      @Valid @RequestBody CreateOrderRequest request
  ) {
    if (authorization == null || !authorization.equals("Bearer token-abc123")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    OrderEntity saved = orders.save(new OrderEntity(request.productId(), request.quantity()));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new OrderResponse(saved.getId().intValue(), "CREATED"));
  }

  public record CreateOrderRequest(@Min(1) int productId, @Min(1) int quantity) {}
  public record OrderResponse(int orderId, String status) {}
}
