FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean install -DskipTests
EXPOSE 8080
CMD ["java", "-jar", "target/disaster-api-1.0-SNAPSHOT.jar"]