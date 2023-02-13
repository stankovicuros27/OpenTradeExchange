FROM maven:3.8.5-openjdk-18 AS build

WORKDIR /stankovicuros27/src/app

COPY . .

RUN mvn clean package

EXPOSE 9999 9998 9997

ENTRYPOINT mvn jetty:run