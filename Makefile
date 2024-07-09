.DEFAULT_GOAL := build-run

# полная пересборка всех контейнеров со всеми зависимости с запуском
restart-full:
	sudo docker-compose up -d --force-recreate --build

# пересборка бекенд контейнера(нужно для обновления кода) с запуском (долгая по времени пересборка из-за зависимостей)
restart-d:
	sudo docker-compose up --detach --build app

# пересборка
restart:
	sudo docker compose down
	sudo docker compose build
	sudo docker compose up -d

# Быстрая пересборка только измененного кода
restart-fast:
	docker-compose up -d --no-deps --build app

.PHONY: build