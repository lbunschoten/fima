FROM openjdk:10.0.2-jre-slim

EXPOSE 80

COPY ./target/fima-transaction-api-17.12.1-SNAPSHOT.jar fima.jar

ENTRYPOINT ["java", "-jar", "fima.jar", "--server.port=8080", "--server.servlet.context-path=/transaction"]
