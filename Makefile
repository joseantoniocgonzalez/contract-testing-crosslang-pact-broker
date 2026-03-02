DC ?= docker-compose
COMPOSE_FILE := infra/docker-compose.yml
ENV_FILE ?= .env
ENV_USED := $(if $(wildcard $(ENV_FILE)),$(ENV_FILE),.env.example)
DC_CMD := $(DC) --env-file $(ENV_USED) -f $(COMPOSE_FILE)

.PHONY: up down logs ps config

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
