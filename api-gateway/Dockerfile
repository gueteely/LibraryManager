FROM openjdk:17-jdk-slim as build

# Установка рабочей директории
WORKDIR /app

# Копирование файлов Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Делаем mvnw исполняемым
RUN chmod +x ./mvnw

# Сборка проекта с пропуском тестов
RUN ./mvnw package -DskipTests

# Переход к финальному образу
FROM openjdk:17-jdk-slim

# Создание директории для приложения
WORKDIR /app

# Копирование JAR из сборочного образа
COPY --from=build /app/target/*.jar app.jar

# Команда для запуска
ENTRYPOINT ["java", "-jar", "app.jar"]