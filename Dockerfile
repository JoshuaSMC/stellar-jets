FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY stellar-jets-backend/pom.xml stellar-jets-backend/
COPY stellar-jets-backend/src stellar-jets-backend/src/
RUN mvn -pl stellar-jets-backend -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/stellar-jets-backend/target/stellar-jets-backend-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
