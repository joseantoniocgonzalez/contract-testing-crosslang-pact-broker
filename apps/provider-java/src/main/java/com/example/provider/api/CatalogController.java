package com.example.provider.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class CatalogController {

  @GetMapping(
      path = "/products/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ProductResponse getProduct(@PathVariable int id) {
    return new ProductResponse(id, "Example Product", new BigDecimal("19.99"));
  }

  public record ProductResponse(int id, String name, BigDecimal price) {}
}
