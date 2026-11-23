FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:25-jre-jammy

WORKDIR /app

RUN useradd -r -u 1001 appuser

RUN mkdir -p /app/logs && chown appuser:appuser /app/logs

COPY --from=builder /build/target/*.jar app.jar
RUN chown appuser:appuser app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]