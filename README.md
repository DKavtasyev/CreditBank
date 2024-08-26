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

[Подробное описание микросервиса deal](calculator/README.md)

### Statement
Отправляет уведомления по электронной почте при ключевых событиях в процессе оформления кредита. 

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
Выполняет прескоринг кредитной заявки. Отсекает пользователей, чьи данные не соответствуют условиям кредита.

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

### Сборка приложения

Порядок действий:
- Установите Docker и docker-compose

[//]: # (- Установите PostgreSQL, создайте базу данных "credit-bank" )
- Добавьте следующие переменные окружения:
  - Пароль от базы данных POSTGRES_PASSWORD
  - Пароль от почтового ящика EMAIL_PASSWORD
- Запустите приложение командой docker-compose up (Docker должен быть запущен)

Пароль от почты EMAIL_PASSWORD необходимо получить у разработчика, пароль