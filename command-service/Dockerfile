FROM openjdk:15-alpine
MAINTAINER "Vitalii Vorobii"
ARG SPRING_BOOT_JAR_FILE=target/*.jar
COPY ${SPRING_BOOT_JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]