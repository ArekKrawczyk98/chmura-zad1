FROM openjdk:11.0.15-jdk-slim-buster as builder
MAINTAINER arkadiusz.krawczyk
WORKDIR /app/
COPY src src
COPY build.gradle.kts build.gradle.kts
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle.kts settings.gradle.kts
RUN sudo ./gradlew clean build


FROM openjdk:11.0.15-jre-slim-buster
WORKDIR /app/
COPY --from=builder /app/build/libs/chmura-1-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
