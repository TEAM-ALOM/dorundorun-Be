FROM openjdk:17

WORKDIR /app/dorun

ARG JAR_PATH=../build/libs
ARG RESOURCES_PATH=../build/resources/main

COPY ${JAR_PATH}/*.jar /app/dorun/dorun.jar

ARG SPRING_PROFILES_ACTIVE=dev
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE

LABEL authors="jangjaesang"

ENTRYPOINT ["java", "-jar", "dorun.jar"]