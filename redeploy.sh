#!/bin/bash

# Путь к проекту
PROJECT_PATH=~/proj/DocAcc/DocumentAccounting2

# Адрес виртуальной машины и пользователь, адрес vm может меняться в текущей сборке, тк пока он динамический - чекать в YC
VM_USER=ivan_vm
VM_IP=84.201.157.0

# Путь на виртуальной машине
VM_PROJECT_PATH=/home/ivan_vm
VM_DOCKER_COMPOSE_FROM_PROJECT_PATH=/home/ivan_vm/DocumentAccounting2

# Копируем проект на виртуальную машину
echo "Копирование проекта на удаленную машину..."
scp -r $PROJECT_PATH $VM_USER@$VM_IP:$VM_PROJECT_PATH

# Подключаемся к виртуальной машине и перезапускаем контейнеры
echo "Перезапуск контейнеров на удаленной машине..."
ssh $VM_USER@$VM_IP << EOF
cd $VM_DOCKER_COMPOSE_FROM_PROJECT_PATH
sudo docker-compose down
sudo docker-compose up --build -d

sudo docker container prune -f
sudo docker image prune -a -f
sudo docker network prune -f
sudo docker volume prune -f
exit
EOF
