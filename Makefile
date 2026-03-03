DC ?= $(if $(shell command -v docker-compose 2>/dev/null),docker-compose,docker compose)
COMPOSE_FILE := infra/docker-compose.yml
ENV_FILE ?= .env
ENV_USED := $(if $(wildcard $(ENV_FILE)),$(ENV_FILE),.env.example)
DC_CMD := $(DC) --env-file $(ENV_USED) -f $(COMPOSE_FILE)

.PHONY: up down logs ps config provider-verify consumer-test can-i-deploy

up:
	@$(DC_CMD) up -d

down:
	@$(DC_CMD) down -v

logs:
	@$(DC_CMD) logs -f --tail=100

ps:
	@$(DC_CMD) ps

config:
	@$(DC_CMD) config >/dev/null && echo "compose config OK (env: $(ENV_USED))"

provider-verify:
	@cd apps/provider-java && \
	  PROVIDER_SHA=$$(git rev-parse HEAD) && \
	  PACT_BROKER_USERNAME=$${PACT_BROKER_USERNAME:-ci} \
	  PACT_BROKER_PASSWORD=$${PACT_BROKER_PASSWORD:-ci} \
	  ./mvnw -q test -Dtest=PactProviderVerificationTest \
	    -Dpactbroker.tags="$$TAG" \
	    -Dpact.verifier.publishResults=true \
	    -Dpact.provider.version="$$PROVIDER_SHA"

consumer-test:
	@cd apps/consumer-python && \
	  if [ ! -d .venv ]; then python3.13 -m venv .venv; fi && \
	  . .venv/bin/activate && \
	  python -m pip install -q -r requirements-dev.txt && \
	  pytest -q && \
	  SHA=$$(git rev-parse HEAD) && \
	  BROKER_URL=$${PACT_BROKER_BASE_URL:-http://localhost:9292} && \
	  USER=$${PACT_BROKER_USERNAME:-ci} && \
	  PASS=$${PACT_BROKER_PASSWORD:-ci} && \
	  TAG=$${PACT_TAG:-local} && \
	  pact-broker publish pacts \
	    --broker-base-url "$$BROKER_URL" \
	    --broker-username "$$USER" \
	    --broker-password "$$PASS" \
	    --consumer-app-version "$$SHA" \
	    --tag "$$TAG"


can-i-deploy:
	@cd apps/consumer-python && \
	  if [ ! -d .venv ]; then python3.13 -m venv .venv; fi && \
	  . .venv/bin/activate && \
	  python -m pip install -q -r requirements-dev.txt && \
	  SHA=$$(git -C ../.. rev-parse HEAD) && \
	  BROKER_URL=$${PACT_BROKER_BASE_URL:-http://localhost:9292} && \
	  USER=$${PACT_BROKER_USERNAME:-ci} && \
	  PASS=$${PACT_BROKER_PASSWORD:-ci} && \
	  pact-broker can-i-deploy \
	    --broker-base-url "$$BROKER_URL" \
	    --broker-username "$$USER" \
	    --broker-password "$$PASS" \
	    --pacticipant consumer-python --version "$$SHA" \
