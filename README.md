# contract-testing-crosslang-pact-broker

[![publish-pacts](https://github.com/joseantoniocgonzalez/contract-testing-crosslang-pact-broker/actions/workflows/publish-pacts.yml/badge.svg?branch=main)](https://github.com/joseantoniocgonzalez/contract-testing-crosslang-pact-broker/actions/workflows/publish-pacts.yml)
[![verify-and-gate](https://github.com/joseantoniocgonzalez/contract-testing-crosslang-pact-broker/actions/workflows/verify-and-gate.yml/badge.svg?branch=main)](https://github.com/joseantoniocgonzalez/contract-testing-crosslang-pact-broker/actions/workflows/verify-and-gate.yml)

Monorepo tipo empresa para *Contract Testing cross-language* con *Pact + Pact Broker, con flujo completo **local + CI*:

*publish pacts → provider verification → can-i-deploy (gating)*

---

## Portfolio highlights (por qué este repo es fuerte)

- *Cross-language real: Consumer en **Python* y Provider en *Java/Spring Boot*.
- *3 contratos completos* (Auth, Catalog, Checkout) con request/response JSON reales.
- *Pact Broker en Docker* (con Postgres) y configuración por .env / variables en CI.
- *Versionado por commit* en Broker:
  - consumerVersion = GIT_SHA
  - providerVersion = GIT_SHA
- *Provider verification desde el Broker* y *publicación de verification results*.
- *Gating real* con can-i-deploy: si un cambio rompe el contrato, *la CI falla*.
- *CI en GitHub Actions* con artifacts (pacts, provider-surefire-reports).
- *Checkout con persistencia: POST /orders persiste en **H2 (JPA)* y devuelve orderId generado.
- *Prueba negativa demostrable*: PR con breaking change (cerrado sin merge) que hizo fallar verify-and-gate.

---

## ¿Qué problema resuelve?

Evita roturas silenciosas entre equipos/servicios.

El *consumer* publica lo que espera (contrato). El *provider* verifica en su build que lo cumple.  
Si no hay verificación válida, *can-i-deploy* bloquea.

---

## Arquitectura

text
Consumer (Python)  --publishes-->  Pact Broker  <--verifies--  Provider (Java)
       |                                                   |
       +-- contract tests (pacts)                           +-- provider verification + publish results


---

## Contratos implementados (3)

### 1) Auth
- POST /login → 200 { "token": "..." }

### 2) Catalog (público)
- GET /products/{id} → 200 { "id": 123, "name": "...", "price": 19.99 }

### 3) Checkout (auth + persistencia)
- POST /orders con Authorization: Bearer token-abc123
- → 201 { "orderId": 1, "status": "CREATED" }
- Persiste pedidos en *H2 (JPA)*

---

## Flujo end-to-end

text
1) pytest (consumer) -> genera pact JSON
2) publish al Broker (consumerVersion=GIT_SHA, tag=branch/local)
3) provider verification desde Broker (providerVersion=GIT_SHA) + publish results
4) can-i-deploy consulta al Broker y decide si se puede desplegar (gating)


---

## Estructura del repo

- apps/consumer-python/
- apps/provider-java/
- infra/ (Pact Broker + Postgres)
- .github/workflows/

---

## Requisitos

- Docker (Compose v2 recomendado)
- GNU Make
- Python 3.13
- Java 21 (Maven Wrapper ./mvnw)

---

## Configuración del Broker (local)

El compose lee .env si existe; si no, usa .env.example.

Credenciales por defecto (local):
- ci / ci (rw)
- dev / dev (readonly)

---

## Make targets

Infra:
- make up / make down / make ps / make logs

Contract testing:
- make consumer-test  
  Ejecuta pytest, genera pacts y publica al Broker.
- make provider-verify  
  Descarga pacts del Broker (según tag) y verifica el provider. Publica resultados.
- make can-i-deploy  
  Gating final contra el Broker: “yes/no”.

Variables útiles:
- PACT_TAG:
  - local: local
  - CI: nombre de rama (github.ref_name)
- PACT_BROKER_BASE_URL (default http://localhost:9292)
- PACT_BROKER_USERNAME / PACT_BROKER_PASSWORD (default ci/ci)

---

## Quickstart (local) — End to End

1) Levantar Broker

bash
make up
make ps


2) Publicar contratos

bash
PACT_TAG=local make consumer-test


3) Verificar provider desde Broker

bash
PACT_TAG=local make provider-verify


4) Gating

bash
make can-i-deploy


---

## Verificación manual rápida (opcional)

Arrancar provider:

bash
cd apps/provider-java
./mvnw spring-boot:run


Probar endpoints:

bash
curl -i -X POST http://localhost:8080/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"jose","password":"secret"}'

curl -i http://localhost:8080/products/123

curl -i -X POST http://localhost:8080/orders \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer token-abc123' \
  -d '{"productId":123,"quantity":2}'


---

## CI (GitHub Actions)

Workflows:

- *publish-pacts* (.github/workflows/publish-pacts.yml)
  - Levanta broker y publica pacts (make consumer-test)
  - Artifact: pacts

- *verify-and-gate* (.github/workflows/verify-and-gate.yml)
  - Publica pacts → verifica provider (make provider-verify) → gating (make can-i-deploy)
  - Si hay breaking change, falla
  - Artifact: provider-surefire-reports

---

## Troubleshooting

- Si provider-verify falla con “Failed to fetch HAL document”, revisa que el Broker esté arriba:
  bash
  make ps
  make up
  
- En CI se usa docker compose (v2). El Makefile autodetecta docker-compose vs docker compose.

---

## Docs

- Pact Python (consumer): https://docs.pact.io/implementation_guides/python/docs/consumer
- Pact Broker CLI: https://docs.pact.io/pact_broker/client_cli
- Pact JVM Provider (JUnit5): https://docs.pact.io/implementation_guides/jvm/provider/junit5
