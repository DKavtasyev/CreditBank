[![codecov ms calculator](https://codecov.io/github/DKavtasyev/CreditBank/branch/feature%2Fadd_docker_and_ci/graph/badge.svg?flag=calculator&token=EG63IGUXHV)](https://codecov.io/github/DKavtasyev/CreditBank) Calculator  \
[![codecov ms deal](https://codecov.io/github/DKavtasyev/CreditBank/branch/feature%2Fadd_docker_and_ci/graph/badge.svg?flag=deal&token=EG63IGUXHV)](https://codecov.io/github/DKavtasyev/CreditBank) Deal   \
[![codecov ms statement](https://codecov.io/github/DKavtasyev/CreditBank/branch/feature%2Fadd_docker_and_ci/graph/badge.svg?flag=statement&token=EG63IGUXHV)](https://codecov.io/github/DKavtasyev/CreditBank) Statement \
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=DKavtasyev_CreditBank&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=DKavtasyev_CreditBank) \
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=DKavtasyev_CreditBank&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=DKavtasyev_CreditBank) \
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=DKavtasyev_CreditBank&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=DKavtasyev_CreditBank)

# <img src="dossier/src/main/resources/static/img/logo.png" style="height: 30px; width: 30px"/> CreditBank

CreditBank — учебное приложение для автоматизированной обработки заявок на кредит и принятия решений на основе анализа
данных заемщика. Разработано на Java с использованием Spring Boot 3.2.6, Docker, PostgreSQL. Обладает
микросервисной архитектурой с синхронным взаимодействием по REST API и асинхронным через Kafka MQ.

Диаграмма покрытия тестами:

[![codecov](https://codecov.io/github/DKavtasyev/CreditBank/graphs/tree.svg?token=EG63IGUXHV)](https://codecov.io/github/DKavtasyev/CreditBank)

## Архитектура
Приложение декомпозировано на пять микросервисов и один общий модуль:
- MS calculator
- MS deal
- MS statement
- MS dossier
- MS gateway
- common

<br>
<div style="display: flex;justify-content: center;">
  <img src="https://github.com/user-attachments/assets/e7ead87e-84af-473e-8c61-c7522a8e33d0" alt="architecture"/>
</div>
<br>

Логика работы приложения:
1. Пользователь отправляет заявку на кредит.
2. Модуль Statement осуществляет прескоринг заявки. Если он успешен, заявка сохраняется в модуле Deal и передаётся в
модуль Calculator.
3. Модуль Calculator рассчитывает и возвращает через модуль Deal пользователю 4 предложения кредита (LoanOfferDto) с
разными условиями или отказ.
4. Пользователь выбирает предложение, информация передаётся в модуль Deal, где данные сохраняются.
5. Модуль Dossier отправляет клиенту уведомление о предварительном одобрении заявки с просьбой завершить оформление.
6. Клиент отправляет полные данные в модуль Deal. Модуль Calculator выполняет скоринг и расчет кредита, данные
сохраняются в Deal со статусом CALCULATED.
7. Модуль Dossier отправляет клиенту письмо с одобрением или отказом, при одобрении добавляется ссылка на запрос
"Сформировать документы".
8. Клиент запрашивает документы, модуль Deal формирует их, Dossier отправляет их на почту вместе со ссылкой на согласие
с условиями.
9. Клиент соглашается или отказывается от условий. При согласии модуль Dossier отправляет код для подписания документов.
10. Если код подтвержден, модуль Deal выдает кредит (меняет статус кредита на ISSUED, а заявки на CREDIT_ISSUED).

<br>
<div style="display: flex;justify-content: center;">
  <img src="https://github.com/user-attachments/assets/d851aaf3-233b-4435-9d92-4ca33e18718d" alt="architecture"/>
</div>
<br>


### Calculator
Рассчитывает параметры кредита. Производит расчёт:
- Кредитных предложений
- Графика платежей по кредиту

<table>
<thead>
    <tr><th>Метод</th><th>Путь</th><th>Описание</th></tr>
</thead>
<tbody>
    <tr><td>POST</td><td>/calculator/offers</td><td>Генерация кредитных предложений</td></tr>
    <tr><td>POST</td><td>/calculator/calc</td><td>Расчёт кредита</td></tr>
</tbody> 
</table>

[Подробное описание микросервиса calculator](calculator/README.md)

### Deal
Управляет сделками по кредиту. Центральный модуль с подключением к PostgreSQL для хранения данных о кредитах.

<table>
<thead>
    <tr><th>Метод</th><th>Путь</th><th>Описание</th></tr>
</thead>
<tbody>
    <tr><td>POST</td><td>/deal/statement</td> <td>Создание заявки</td></tr>
    <tr><td>POST</td><td>/deal/offer/select</td> <td>Выбор кредитного предложения</td></tr>
    <tr><td>POST</td><td>/deal/calculate/{statementId}</td> <td>Расчёт кредита</td></tr>
    <tr><td>GET</td><td>/deal/offer/deny/{statementId}</td> <td>Отказ пользователя от кредита</td></tr>
    <tr><td>POST</td><td>/deal/document/{statementId}/send</td> <td>Запрос на формирование и отправку документов</td></tr>
    <tr><td>POST</td><td>/deal/document/{statementId}/sign</td> <td>Запрос на подписание документов</td></tr>
    <tr><td>POST</td><td>/deal/document/{statementId}/code</td> <td>Подписание документов</td></tr>
    <tr><td>PUT</td><td>/deal/admin/statement/{statementId}/status</td> <td>Установка статуса заявки</td></tr>
    <tr><td>GET</td><td>/deal/admin/statement/{statementId}</td> <td>Запрос заявки по её id</td></tr>
    <tr><td>GET</td><td>/deal/admin/statement</td> <td>Запрос всех заявок</td></tr>
</tbody>
</table>

[Подробное описание микросервиса deal](deal/README.md)

### Statement
Выполняет прескоринг кредитной заявки. Отсекает пользователей, чьи данные не соответствуют условиям кредита.

<table>
<thead>
    <tr><th>Метод</th><th>Путь</th><th>Описание</th></tr>
</thead>
<tbody>
    <tr><td>POST</td><td>/statement</td><td>Создание заявки</td></tr>
    <tr><td>POST</td><td>/statement/offer</td><td>Выбор кредитного предложения</td></tr>
</tbody> 
</table>

[Подробное описание микросервиса statement](statement/README.md)

### Dossier
Отправляет уведомления по электронной почте при ключевых событиях в процессе оформления кредита.

<table>
<thead>
    <tr><th>Kafka topic</th><th>Описание</th><th>отправка email</th></tr>
</thead>
<tbody>
    <tr><td>finish-registration</td><td>Запрос на завершение оформления кредита</td><td>Да</td></tr>
    <tr><td>create-documents</td><td>Запрос на создание документов</td><td>Да</td></tr>
    <tr><td>send-documents</td><td>Отправка документов</td><td>Да</td></tr>
    <tr><td>send-ses</td><td>Запрос на подписание документов</td><td>Да</td></tr>
    <tr><td>credit-issued</td><td>Проверка подписи</td><td>Да</td></tr>
    <tr><td>statement-denied</td><td>Отказ пользователя от кредита</td><td>Да</td></tr>
</tbody> 
</table>

[Подробное описание микросервиса dossier](dossier/README.md)

### Gateway
Маршрутизирует запросы от пользователя к соответствующим микросервисам.

<table>
<thead>
    <tr><th>Метод</th><th>Путь</th><th>Описание</th><th>Маршрутизация</th></tr>
</thead>
<tbody>
    <tr><td>POST</td><td>/statement</td> <td>Создание заявки</td><td>MS statement</td></tr>
    <tr><td>POST</td><td>/statement/select</td> <td>Выбор кредитного предложения</td><td>MS statement</td></tr>
    <tr><td>POST</td><td>/registration/{statementId}</td> <td>Расчёт кредита</td><td>MS deal</td></tr>
    <tr><td>GET</td><td>/deny/{statementId}</td> <td>Отказ пользователя от кредита</td><td>MS deal</td></tr>
    <tr><td>POST</td><td>/document/{statementId}</td> <td>Запрос на формирование и отправку документов</td><td>MS deal</td></tr>
    <tr><td>POST</td><td>/document/{statementId}/sign</td> <td>Запрос на подписание документов</td><td>MS deal</td></tr>
    <tr><td>POST</td><td>/document/{statementId}/sign/code</td> <td>Подписание документов</td><td>MS deal</td></tr>
    <tr><td>PUT</td><td>/admin/statement/{statementId}/status</td> <td>Установка статуса заявки</td><td>MS deal</td></tr>
    <tr><td>GET</td><td>/admin/statement/{statementId}</td> <td>Запрос заявки по её id</td><td>MS deal</td></tr>
    <tr><td>GET</td><td>/admin/statement</td> <td>Запрос всех заявок</td><td>MS deal</td></tr>
</tbody>
</table>

[Подробное описание микросервиса gateway](gateway/README.md)

---
## Сборка и запуск приложения

### Запуск в Docker

- Установить Docker и docker-compose
- Добавить следующие переменные окружения:
  - Пароль от базы данных POSTGRES_PASSWORD, можно задать любой
  - Пароль от почтового ящика EMAIL_PASSWORD - необходимо получить у разработчика
- Перейти в корневую директорию проекта
  ```bash
  cd $(git rev-parse --show-toplevel)
  ```
- запустить контейнеры Docker с модулями
  ```bash
  docker-compose up -d
  ```
После выполнения команд будут запущены и доступны следующие модули:
- kafka
- kafdrop
- zookeeper
- prometheus
- grafana
- postgres
- calculator
- deal
- statement
- dossier
- gateway

### Запуск в Intellij Idea
- Установить Docker и docker-compose
- Добавить следующие переменные окружения:
  - Пароль от базы данных POSTGRES_PASSWORD, можно задать любой
  - Пароль от почтового ящика EMAIL_PASSWORD - необходимо получить у разработчика
- Перейти в корневую директорию проекта
  ```bash
  cd $(git rev-parse --show-toplevel)
  ```
- Запустить контейнеры Docker с MQ Kafka, Kafdrop
   ```bash
   docker-compose -f ./deal/docker-compose.yaml up
   ```
- Запустить контейнеры Docker с Prometheus, Grafana (необязательно)
  ```bash
  docker-compose -f ./scripts/docker-compose.yaml up
  ```
- Собрать проект в Maven и запустить все модули
  ```bash
  mvn clean install -Dskiptests
  ```
  
### Документация API
Для просмотра и тестирования доступных API методов используется Swagger UI. \
Адрес доступа к Swagger:
> http://localhost:8080/swagger-ui/index.html

### Мониторинг и Визуализация с Grafana
Для мониторинга и визуализации данных используется Grafana. \
Адрес доступа к Grafana:
> http://localhost:3000

#### Настройка Grafana
1. Убедиться, что контейнер с Grafana запущен (см. "Запуск в Docker", "Запуск в Intellij Idea").
2. Открыть Grafana в браузере по адресу http://localhost:3000.
3. Данные для входа по умолчанию:
   - Логин: admin
   - Пароль: admin
4. Создать новый дашборд для мониторинга интересующих метрик.

### Визуализация и Управление Apache Kafka с Kafdrop
Для визуализации и управления кластерами Apache Kafka используется Kafdrop. \
Адрес доступа к Kafdrop:

> http://localhost:9000