FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests
EXPOSE $PORT
CMD ["sh", "-c", "java -jar target/disaster-api-1.0-SNAPSHOT.jar"]
