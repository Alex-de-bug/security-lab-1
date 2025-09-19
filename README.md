# Информационная безопасность. Работа 1. P3412. Дениченко А. О.

## Разработка защищенного REST API с интеграцией в CI/CD (Spring Boot 3)
REST‑сервис с аутентификацией по JWT, демо CRUD над сущностью `Post` и базовыми мерами защиты от SQL‑инъекций и XSS.

### Технологии
- **Spring Boot 3**, **Spring Security 6**, **Spring Data JPA (Hibernate)**
- **H2** (in-memory)
- **JWT** HS256
- **OWASP Java Encoder** для экранирования (XSS)
- **BCrypt** для хеширования паролей
- **springdoc-openapi** (Swagger UI)

## Быстрый старт
1) Требуется JDK 17+.

2) Запуск:
```bash
cd simpleapi
./mvnw spring-boot:run
```
Приложение слушает `http://localhost:8080`.

Swagger: `http://localhost:8080/swagger-ui.html`

H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, user: `sa`, password: пусто)

3) При первом старте создаются демо‑пользователи и посты (`DataInitializer`), в т.ч.:
- `admin / password123`

## Переменные окружения
Можно переопределить значения из `application.properties` через ENV/JVM-параметры:
- `jwt.secret` — секрет для подписи JWT (HS256). По умолчанию задан в `application.properties`.
- `jwt.expiration` — время жизни access‑токена в мс (по умолчанию `86400000`).
- `server.port` — порт приложения (по умолчанию `8080`).

Примеры:
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--jwt.secret=... --jwt.expiration=3600000"
# или через переменные окружения
export JWT_SECRET=...; export JWT_EXPIRATION=3600000
```

## API
Все эндпоинты, кроме `/auth/**`, `/v3/api-docs/**`, `/swagger-ui/**`, `/h2-console/**` — требуют заголовок `Authorization: Bearer <JWT>`.

### Аутентификация
- POST `/auth/login`
  - Тело:
    ```json
    { "username": "admin", "password": "password123" }
    ```
  - Успех (200):
    ```json
    { "token": "<jwt>", "username": "admin", "email": "admin@example.com" }
    ```
  - Ошибка (400): `{"message":"Invalid username or password"}`
  - Пример:
    ```bash
    curl -X POST http://localhost:8080/auth/login \
      -H "Content-Type: application/json" \
      -d '{"username":"admin","password":"password123"}'
    ```

- POST `/auth/register`
  - Тело:
    ```json
    { "username":"newuser", "email":"new@user.test", "password":"secret" }
    ```
  - Ответы: 200 (успех) / 400 (занят логин/email)

### Демо‑данные и посты
- GET `/api/data` — список всех постов (для демонстрации).
  ```bash
  curl http://localhost:8080/api/data -H "Authorization: Bearer <jwt>"
  ```

- POST `/api/posts` — создать пост от имени текущего пользователя.
  - Тело:
    ```json
    { "title":"<b>Hello</b>", "content":"Hi <script>alert(1)</script>" }
    ```
  - Ответ (200):
    ```json
    {
      "message": "Post created successfully",
      "post": {
        "id": 1,
        "title": "&lt;b&gt;Hello&lt;/b&gt;",
        "content": "Hi &lt;script&gt;alert(1)&lt;/script&gt;",
        "createdAt": "2025-09-19T12:00:00",
        "authorUsername": "admin"
      }
    }
    ```

- GET `/api/posts/my` — посты текущего пользователя.

- GET `/api/profile` — профиль текущего пользователя.

## Реализованные меры защиты

### Аутентификация и управление сессией
- **JWT Bearer**: stateless‑аутентификация через `Authorization: Bearer <jwt>`.
- **Политика доступа**: открыт `/auth/**`, Swagger и H2; остальное — аутентификация (`WebSecurityConfig`).
- **Подпись и проверка токенов**: HS256, тип `access`, срок из `jwt.expiration` (`JwtUtils`).
- **Пароли**: хеш `BCrypt` (см. `PasswordEncoder`).
- **Stateless**: `SessionCreationPolicy.STATELESS`.

### Защита от SQL‑инъекций
- **ORM/параметризация**: Spring Data JPA (`PostRepository`, `UserRepository`) — без сырого SQL.
- **Отсутствие конкатенации SQL**: пользовательский ввод не попадает в SQL‑строки.
- **Валидация**: DTO помечены `@Valid` (ошибки — 400).

### Защита от XSS
- **Экранирование**: текстовые поля (`title`, `content`) кодируются OWASP Java Encoder в `Sanitizer` перед сохранением/отдачей.
- **JSON‑ответы**: принцип encode‑on‑output сохраняется при дальнейшем отображении в HTML/DOM на клиенте.

### Прочее
- **Swagger/OpenAPI**: авто‑документация (`/swagger-ui.html`).
- **H2**: in‑memory БД для простого запуска; демо‑данные (`DataInitializer`).

## Отчёты SAST/SCA
SCA-анализ зависимостей выполнен Snyk:
- Начальный отчёт с найденными уязвимостями: `simpleapi/src/main/resources/snyk-report-2.json`
- Финальный отчёт без уязвимостей: `simpleapi/src/main/resources/snyk-report-3.json`

SAST-анализ зависимостей выполнен :
- Начальный отчёт с найденными уязвимостями: `simpleapi/src/main/resources/spotbugsXml.xml`

total_bugs='0'

## Известные ограничения
- Нет обновления/удаления постов (только создание и чтение).
- Единая роль для всех аутентифицированных пользователей.
- Нет refresh‑токенов/отзыва токенов.

## Полезные ссылки
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`


