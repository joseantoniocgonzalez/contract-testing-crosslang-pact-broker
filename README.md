# contract-testing-crosslang-pact-broker

Monorepo tipo empresa para **Contract Testing cross-language** con **Pact + Pact Broker**.

- **Consumer**: Python (pytest) genera y publica contratos (pacts) al Broker.
- **Provider**: Java (Spring Boot) verificará esos contratos desde el Broker. *(pendiente)*
- **CI**: publish pacts → provider verification → can-i-deploy (gating). *(pendiente)*

## ¿Qué problema resuelve?

Evita roturas silenciosas entre equipos/servicios: el consumer define **qué espera** (request/response) y el provider valida en su build que **sigue cumpliéndolo** antes de desplegar.

## Arquitectura (alto nivel)

```text
Consumer (Python)  --publishes-->  Pact Broker  <--verifies--  Provider (Java)
       |                                                   |
       +-- contract tests (pacts)                           +-- provider verification
```

## Flujo de trabajo

```text
1) Consumer tests (pytest) -> generan pact JSON
2) Publicar pact al Broker (consumerVersion = GIT_SHA, tag = branch/local)
3) Provider verifica desde Broker (providerVersion = GIT_SHA)
4) can-i-deploy decide si se puede desplegar (gating)
```

## Estructura del repo

- `apps/consumer-python/`
- `apps/provider-java/` *(pendiente)*
- `infra/` (Pact Broker + Postgres)
- `.github/workflows/` *(pendiente)*

## Requisitos

- Docker + docker-compose
- Python 3.13
- Java 21 + Maven *(para provider; pendiente)*

## Quickstart (local)

### 1) Levantar Pact Broker

```bash
make up
make ps
```

Broker local: `http://localhost:9292`

Credenciales por defecto (local):
- `ci` / `ci` (rw)
- `dev` / `dev` (readonly)

Variables en `.env.example`. En local puedes crear `.env` (no se commitea) para tus valores.

### 2) Consumer (Auth contract: POST /login)

```bash
cd apps/consumer-python
python3.13 -m venv .venv
source .venv/bin/activate
python -m pip install -U pip
python -m pip install -r requirements-dev.txt

pytest -q tests/test_pact_auth.py
```

### 3) Publicar el pact al Broker (local)

```bash
cd apps/consumer-python
source .venv/bin/activate

SHA=$(git rev-parse HEAD)
pact-broker publish pacts \
  --broker-base-url http://localhost:9292 \
  --broker-username ci \
  --broker-password ci \
  --consumer-app-version "$SHA" \
  --tag local
```

## Versionado y tags (Broker)

- `consumerVersion = GITHUB_SHA`
- `providerVersion = GITHUB_SHA` *(pendiente)*
- Tags: nombre de rama; en local: `local`

## Estado actual

✅ Infra Pact Broker local (docker-compose + Makefile)  
✅ Consumer Python: contrato Auth `/login` (genera + publica pact)  
⏳ Provider Java: implementación + verificación desde Broker  
⏳ can-i-deploy + CI GitHub Actions (publish → verify → gate)

## Make targets

- `make up` / `make down` / `make ps` / `make logs`

Docs:
- Pact Python (consumer): https://docs.pact.io/implementation_guides/python/docs/consumer
- Pact Broker CLI: https://docs.pact.io/pact_broker/client_cli
