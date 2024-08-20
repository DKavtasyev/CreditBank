[![codecov ms calculator](https://codecov.io/github/DKavtasyev/CreditBank/branch/feature%2Fadd_docker_and_ci/graph/badge.svg?flag=calculator&token=EG63IGUXHV)](https://codecov.io/github/DKavtasyev/CreditBank) МС Calculator  \
[![codecov ms deal](https://codecov.io/github/DKavtasyev/CreditBank/branch/feature%2Fadd_docker_and_ci/graph/badge.svg?flag=deal&token=EG63IGUXHV)](https://codecov.io/github/DKavtasyev/CreditBank) МС Deal   \
[![codecov ms statement](https://codecov.io/github/DKavtasyev/CreditBank/branch/feature%2Fadd_docker_and_ci/graph/badge.svg?flag=statement&token=EG63IGUXHV)](https://codecov.io/github/DKavtasyev/CreditBank) МС Statement

# CreditBank

CreditBank - это приложение автоматизированной системы обработки заявок на кредитование и принятия решений
на основе комплексного анализа данных заёмщика. \
Приложение написано на Java с использованием Spring Boot v 3.2.6, Docker, PostgreSQL, имеет микросервисную архитектуру.
Взаимодействие между микросервисами: синхронное, по REST API, и асинхронное с использованием MQ Kafka.
Это учебный проект, он свободен для 

## Архитектура
Выполнена декомпозиция на пять микросервисов и один общий модуль. 



Диаграмма покрытия тестами:

[![codecov](https://codecov.io/github/DKavtasyev/CreditBank/graphs/tree.svg?token=EG63IGUXHV)](https://codecov.io/github/DKavtasyev/CreditBank)