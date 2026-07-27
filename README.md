### Hexlet tests and linter status:
[![Actions Status](https://github.com/snevg04/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/snevg04/java-project-99/actions)

### SonarQube check:
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=snevg04_java-project-99&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=snevg04_java-project-99)

### GitHub workflows CI:
[![GitHub workflows CI](https://github.com/snevg04/java-project-99/actions/workflows/build.yml/badge.svg)](https://github.com/snevg04/java-project-99/actions/workflows/build.yml)


# Менеджер задач
Приложение представляет собой трекер задач, который поддерживает назначение исполнителей и статусов для любой задачи. Для удобной работы реализована фильтрация задач по названию, исполнителю, статусам и ярлыкам. Помимо этого поддерживается возможность кастомизации в виде создания своих статусов и ярлыков. Менеджер задач реализован на Spring Boot 4

## Запуск приложения
Приложение можно запустить локально при помощи команды ./gradlew bootRun. При локальном запуске будет использоваться база данных H2

## Доступ к приложению
Демонстрационный вариант приложения доступен по ссылке: https://task-manager-dzkk.onrender.com
При локальном запуске следует использовать localhost:8080

## Данные для входа
Имя пользователя: hexlet@examlpe.com
Пароль: qwerty
