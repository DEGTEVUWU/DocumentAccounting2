.DEFAULT_GOAL := build-run

# полная пересборка всех контейнеров со всеми зависимости с запуском
restart-full:
	sudo docker-compose up -d --force-recreate --build

# пересборка бекенд контейнера(нужно для обновления кода) с запуском (долгая по времени пересборка из-за зависимостей)
restart-d:
	sudo docker-compose up --detach --build app

# пересборка
restart:
	sudo docker-compose down
	sudo docker-compose build
	sudo docker-compose up -d

# Быстрая пересборка только измененного кода
restart-fast:
	sudo docker-compose up -d --no-deps --build app

logs:
	sudo docker-compose logs

stop:
	sudo docker-compose stop

start:
	sudo docker-compose start

run:
	sudo docker-compose up --build -d

run-f:
	sudo docker-compose up -d

down:
	sudo docker-compose down

log:
	sudo docker-compose logs


.PHONY: build