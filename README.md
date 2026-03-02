# contract-testing-crosslang-pact-broker

Monorepo para contract testing cross-language con Pact + Pact Broker:
- Consumer: Python (pytest) publica pacts al Broker
- Provider: Java (Spring Boot) verifica pacts desde el Broker
- CI: publish → verify → can-i-deploy (gating)

Estructura:
- apps/consumer-python/
- apps/provider-java/
- infra/   (Pact Broker + Postgres)
- .github/workflows/
