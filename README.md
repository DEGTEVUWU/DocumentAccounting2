## Проект для работы с документами

[![Deploy to Yandex Cloud](https://github.com/DEGTEVUWU/DocumentAccounting2/actions/workflows/deploy.yml/badge.svg)](https://github.com/DEGTEVUWU/DocumentAccounting2/actions/workflows/deploy.yml)
[![Maintainability](https://api.codeclimate.com/v1/badges/16e4e661c847bf77836f/maintainability)](https://codeclimate.com/github/DEGTEVUWU/DocumentAccounting2/maintainability)

17.07.2024
### Возможности приложения
- при запуске автоматически создаются два пользователя с ролями "администратор" и "модератор"
- регистрация и авторизации пользователей с присвоением роли "пользователь"
- создание авторизованными пользователями новых документов, редактирование и удаление
- настройка области видимости созданных документов - "публичный документ" или "виден только определённым пользователям", по умолчанию созданный документ виден только автору или администатору
- добавлены ограничения для редактирования и удаления документов:
   - это доступно только автору документа или администратору
   - пользователю доступны для редактирования не все поля(например, только администратор может изменить автора документа)
- добавлен поиск по всем основным полям документов, сортировка отображения по полям, способ сортировки(по возрастанию или обратный), пагинация при поиске
- есть возможность редактирования пользователя: 
   - для роли "пользователь" доступны основные поля для редактрования и удаление только своих данных из базы
   - для роли "администратор" доступно полное редактирование полей пользователя, включая присвоение тому новых ролей, а также удаление любыз пользователейиз базы
- добавлена фронтенд-часть для взаимодействия с пользователем

### Использующиеся технологии
- Фреймворк: **Spring Boot**
- Аутентификация: **Spring Security**, **JWT-токен**
- Работа с базой данных **Spring Data JPA-Hibernate**
- Автоматический маппинг: **Mapstruct**
- Шаблон проектирования: **DTO**
- Поиск, сортировка сущностей из БД **JPA Criteria API**
- Общий обработчик ошибок реализован через метод **AOP**
- Фронтенд-часть **HTML + CSS + JS**
- Документация по API-приложения: **Springdoc Openapi**, **Swagger**
- Тесты: **JUnit 5**, **Mockwebserver**
- Базы данных: **PostgreSQL** 
- Среды разработки **development**, **test**, **production**
- Выполнение разработки возможно, как при подключении локальной БД PostgreSQL, так и через Docker Compose
- Развертывание в production: **Docker**, **Docker Compose**, **Yandex Cloud**
- [Задеплоено](http://158.160.126.62:8080/) на виртуальную машину на серверах Яндекса
- Для редеплоя используются два способа - локальный через bash-скрипты удаленный через GitHub Actions 

### Инструкция по локальному развертыванию
- #### при использовании локальной базы данных
Создать БД с атрибутами:
```
name: docs
user: ivan
password: password
```
Или изменить данные значения в файле application-development.properties на свои  

- #### при запуске через Docker Compose

Прежде всего, скачайте Docker Desktop, установите и запустите его.  
Расскоментируйте значения конфигарции подключения к БД в файле `application-development.properties` с пометкой `data for start in docker-compose` и закомендируйте/удалите текущие данные   
Поднимите приложение командой:   
```
sudo docker-compose up --build -d
или
make run
```
При первом запуске процесс может быть долгим, далее он будет быстрее.  

#### Дополнительная информация
Документация по API/Swagger доступены по адресам при запущенном приложении 
``` 
localhost:8080/v3/api-docs
localhost:8080/swagger-ui/index.html
```
Для входа под админом, используйте данные:
```  
username: admin
password: password
```

Обо всех моментах, уточнениях, пожелания или идеях по улучшению проекта, пишите мне [в телеграм](https://t.me/ar_terria)




