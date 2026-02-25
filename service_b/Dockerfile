# Stage 1: Build
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# Копируем gradle файлы и исходники
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
COPY src /app/src

# Сборка приложения (fat JAR)
RUN ./gradlew clean bootJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Копируем JAR из билд-слоя
COPY --from=build /app/build/libs/*.jar app.jar

# Открываем порт приложения
EXPOSE 8080

# Запускаем Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
