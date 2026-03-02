DC ?= docker-compose
COMPOSE_FILE := infra/docker-compose.yml
ENV_FILE ?= .env

.PHONY: up down logs ps

up:
	@# Usa .env si existe; si no, tira de .env.example
	@if [ -f "$(ENV_FILE)" ]; then \
	  $(DC) --env-file $(ENV_FILE) -f $(COMPOSE_FILE) up -d; \
	else \
	  $(DC) --env-file .env.example -f $(COMPOSE_FILE) up -d; \
	fi

down:
	@$(DC) -f $(COMPOSE_FILE) down -v

logs:
	@$(DC) -f $(COMPOSE_FILE) logs -f --tail=100

ps:
	@$(DC) -f $(COMPOSE_FILE) ps
