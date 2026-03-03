package com.example.provider.contract;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junit5.HttpTestTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@Provider("provider-java")
@PactBroker(
  host = "localhost",
  port = "9292",
  scheme = "http",
  tags = {"local"},
  authentication = @PactBrokerAuth(
    username = "${PACT_BROKER_USERNAME}",
    password = "${PACT_BROKER_PASSWORD}"
  )
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
.com.dius.pact.provider.junitsupport.loader.PactVerificationSpringProvider
public class PactProviderVerificationTest {

  @LocalServerPort
  int port;

  @BeforeEach
  void setup(PactVerificationContext context) {
    context.setTarget(new HttpTestTarget("localhost", port));
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void verifyPact(PactVerificationContext context) {
    context.verifyInteraction();
  }

  @State("user exists")
  void userExists() {
    // No-op por ahora: solo necesitamos que exista el handler del state
  }
}
