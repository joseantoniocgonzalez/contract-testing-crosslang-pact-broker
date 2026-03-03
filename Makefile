DC ?= docker-compose
COMPOSE_FILE := infra/docker-compose.yml
ENV_FILE ?= .env
ENV_USED := $(if $(wildcard $(ENV_FILE)),$(ENV_FILE),.env.example)
DC_CMD := $(DC) --env-file $(ENV_USED) -f $(COMPOSE_FILE)

.PHONY: up down logs ps config provider-verify

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
	    -Dpact.verifier.publishResults=true \
	    -Dpact.provider.version="$$PROVIDER_SHA"
