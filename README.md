# Store System — Automation Tests

Автоматизированное тестирование веб-приложения Store System с использованием Java. Проект включает API- и UI-тесты, отчёты Allure и запуск тестов через Jenkins.

## Технологии

- Java 21
- Maven
- JUnit 5
- REST Assured
- Selenide
- Selenium WebDriver
- Allure Reports
- Docker и Selenoid
- Jenkins
- PostgreSQL для проверки данных пользователей

## Структура проекта

```text
src
└── test
    ├── java
    │   ├── api
    │   │   ├── BaseApiTest.java
    │   │   ├── ProductsApiTest.java
    │   │   └── SuppliersApiTest.java
    │   ├── client
    │   │   ├── AuthClient.java
    │   │   └── ProductClient.java
    │   ├── config
    │   │   ├── Config.java
    │   │   └── SelenideConfig.java
    │   ├── data
    │   ├── model
    │   ├── ui
    │   │   ├── pages
    │   │   └── tests
    │   └── utils
    └── resources
        ├── allure.properties
        └── config.properties
```

## Покрытие тестами

### API
- Создание и получение товаров.
- Обновление данных и доступности товара.
- Создание поставщиков.
- Проверка сценария создания поставщика с дублирующимся именем.

### UI и сквозные сценарии
- Регистрация пользователя.
- Авторизация пользователя.
- Сквозной сценарий регистрации и входа.
- Проверка сохранения пользователя в базе данных.

## Требования

Перед запуском необходимо установить:

- JDK 21
- Git
- Docker Desktop
- Запущенное приложение Store System и необходимые сервисы
- Maven (можно использовать Maven Wrapper или Maven из IntelliJ IDEA, если он настроен)

## Конфигурация

Базовые URL API задаются в `src/test/resources/config.properties`.

По умолчанию:

```properties
auth.base.url=http://localhost:8001
products.base.url=http://localhost:8002
```

Значения можно переопределять системными свойствами Maven. Например, для запуска в Docker/Jenkins используются адреса сервисов, доступные из среды выполнения тестов.

## Запуск тестов

Запустить все тесты:

```bash
mvn clean test
```

Для запуска в удалённом браузере через Selenoid и с настройками для CI можно передать параметры:

```bash
mvn clean test \
  -Dauth.base.url=http://host.docker.internal:8001 \
  -Dproducts.base.url=http://host.docker.internal:8002 \
  -Dselenide.remote=http://host.docker.internal:4444/wd/hub \
  -Ddb.url=jdbc:postgresql://host.docker.internal:5435/strg_users_db
```

Указанные адреса предназначены для соответствующей локальной Docker/Jenkins-конфигурации. При другом окружении их нужно изменить.

## Allure Reports

После запуска тестов результаты Allure сохраняются в `allure-results/`.

Если Allure CLI установлен, сформировать и открыть отчёт можно командами:

```bash
allure serve allure-results
```

Папки `allure-results/` и `allure-report/` не следует хранить в Git: они генерируются при запуске тестов.

## Jenkins и Selenoid

Проект запускался в Jenkins с использованием Java 21, Maven и удалённого браузера через Selenoid. Результаты тестов и отчёт Allure доступны в соответствующей сборке Jenkins.

## Репозиторий

[StoreSystem-Automation на GitHub](https://github.com/Kurpatow/StoreSystem-Automation)
