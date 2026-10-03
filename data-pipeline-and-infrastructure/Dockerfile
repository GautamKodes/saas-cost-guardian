# FROM maven:3.9.6-eclipse-temurin-17 AS builder
# WORKDIR /app
# COPY pom.xml .
# COPY arc ./src
# COPY .mvn ./.mvn
# COPY mvnw .
# RUN chmod +x mvnw && ./mvnw clean package -DskipTests
#
#
# FROM eclipse-temurin:17-jre-alpine
# WORKDIR /app
# COPY --from=builder /app/target/*.jar app.jar
# EXPOSE 8081
# ENTRYPOINT ["java", "-jar", "app.jar"]