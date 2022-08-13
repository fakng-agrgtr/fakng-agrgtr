FROM openjdk:18-alpine
WORKDIR /app
COPY /build/libs/fakng-agrgtr-0.0.1-SNAPSHOT-plain.jar /app/agrgtr.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "agrgtr.jar"]