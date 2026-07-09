FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
EXPOSE $PORT
CMD ["sh", "-c", "java -jar target/disaster-api-1.0-SNAPSHOT.jar"]
