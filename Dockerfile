FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ENV PORT=8082

RUN addgroup -S app && adduser -S app -G app

COPY --from=build /app/target/hola-mundo-devops-1.0.0.jar app.jar

USER app

EXPOSE 8082

CMD ["java", "-jar", "app.jar"]
