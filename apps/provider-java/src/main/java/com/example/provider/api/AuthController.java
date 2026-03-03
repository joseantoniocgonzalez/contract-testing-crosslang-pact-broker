package com.example.provider.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

  @PostMapping(
      path = "/login",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    // Token simple (no JWT/OAuth): suficiente para contract testing
    return new LoginResponse("token-abc123");
  }

  public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
  public record LoginResponse(String token) {}
}
