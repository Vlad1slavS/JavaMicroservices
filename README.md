# Pet Project: Kafka + REST + 3 БД

## Что лежит в репе
- docker-compose файл для поднятия в контейнерах сервисов и БД
- Организована структура микросервисов, внутри них build.gradle в который нужно добавить зависимости, а в папке service_x необходимо написать код
- В application.yaml указаны основные проперти и затянуты переменные среды из контейнеров, которые необходимы для реализации

## Техническое задание
Собрать демо-систему из 3 микросервисов с взаимодействием через Kafka и REST API

### Цель
- Потрогать Kafka как шину событий
- Пощупать взаимодействие REST между сервисами
- Освоить работу с PostgreSQL, Redis, MongoDB
- Развернуть всё через Docker Compose

### Стек технологий
- Spring Boot (SLF4J для логов)
- Docker, Docker Compose
- Kafka, Kafka UI (Redpanda Console)
- MongoDB, Redis, PostgreSQL
- Liquibase (для миграций PostgreSQL)

### Микросервисы
#### Service A
- Слушает Kafka topic `in`
- Сохраняет сообщение в MongoDB
- Вызывает REST API Service B (`POST /api/process`)

#### Service B
- Принимает REST-запросы от Service A
- Если `type == important`, сохраняет в Redis (TTL=5 мин)
- Вызывает Service C по REST (`POST /api/save`)

#### Service C
- Принимает REST-запросы от Service B
- Сохраняет сообщение в PostgreSQL
- Публикует сообщение в Kafka (topic `out`)

### Kafka Topics
| Topic | Publisher   | Consumer         |
|-------|-------------|------------------|
| in    | UI          | Service A        |
| out   | Service C   | UI(для просмотра)|

### Инструкция по запуску
1. Установить Docker и Docker Compose
2. Запустить проект командой или через UI IDE
```bash
docker-compose up --build
```
3. Адрес Kafka UI: http://localhost:8080
4. Отправить тестовое сообщение в Kafka через Kafka UI (`in`)
5. Проверить обработку сообщений через Kafka UI (`out`) и базы данных

### Рекомендации по реализации и запуску
- `docker-compose.yml` — инфраструктура (Kafka, базы, UI), в нем же объявлены необходимые переменные среды (енвы),
  которые можно затянуть в приложение через application.yaml
- `.service_a`, `.service_b`, `.service_c` — код микросервисов
- Для локальной разработки, сначала в градле нужно зарегистрировать все папки как отдельные проекты (жми на build.gradle ПКМ и потом - Link Gradle Project)