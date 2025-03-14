# Automation QA Java API

Автотесты API для проверки веб-приложения [**Stellar Burgers**](https://stellarburgers.nomoreparties.site/)

Эндпоинты описаны в [**документации API**](https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf)

### Задачи
* Протестировать необходимые эндпоинты (проверить тело и код ответа)
* Обеспечить независимость тестов
* Сформировать Allure-отчёт

## Стек
* Java 11
* Maven 4.0.0
* JUnit 4.13.2
* RestAssured 5.3.2
* Allure 2.15.0
* Gson 2.10.1
* Lombok 1.18.34
* javafaker 1.0.2

## Запуск тестов и построение отчёта
* mvn clean test
* mvn allure:serve