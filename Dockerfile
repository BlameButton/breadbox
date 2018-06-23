FROM maven:latest

COPY ./ /var/app
WORKDIR /var/app
RUN mvn clean install

ENTRYPOINT java -jar /var/app/target/breadbox.jar