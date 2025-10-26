FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build-app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /build-app/target/vhs-rental-system-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
