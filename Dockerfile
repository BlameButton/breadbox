FROM maven:latest AS build

COPY ./ /var/app
WORKDIR /var/app
RUN mvn clean install

FROM openjdk:8

COPY --from=build /var/app/target/breadbox.jar /var/app

ENTRYPOINT java -jar /var/app/target/breadbox.jar