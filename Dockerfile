# Use a modern LTS Java version with JRE (smaller than JDK)
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p /app && chown -R spring:spring /app
WORKDIR /app
USER spring:spring
COPY --chown=spring:spring ./app/target/OrderBook-0.0.1-SNAPSHOT.jar ./app.jar

ENTRYPOINT ["java", "-jar", "./app.jar"]
