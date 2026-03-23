# syntax=docker/dockerfile:1

# -----------------------------------------------------------------------------
# Stage 1: build con Maven + Java 17 (OpenAPI Generator se ejecuta en compile)
# -----------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Empaquetado sin tests (OpenAPI Generator corre en generate-sources / compile)
RUN mvn -q -B -DskipTests package

RUN cp target/payment-initiation-service-*.jar /app/app.jar

# -----------------------------------------------------------------------------
# Stage 2: runtime solo JRE 17
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring \
    && chown -R spring:spring /app

COPY --from=build --chown=spring:spring /app/app.jar /app/app.jar

USER spring:spring

# Misma configuración que application.yml (server.port: 8080)
EXPOSE 8080

# JVM acotada para contenedor pequeño (ajustar según host)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/actuator/health >/dev/null || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
