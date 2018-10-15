FROM maven:latest AS build

COPY . .
RUN mvn clean install

FROM openjdk:8-jre-alpine

ENV BREADBOX_TOKEN=$BREADBOX_TOKEN
COPY --from=build target/breadbox.jar app.jar

CMD ["java", "-jar", "app.jar"]