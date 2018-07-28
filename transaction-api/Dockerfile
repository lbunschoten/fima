FROM openjdk:10.0.2-jre-slim

EXPOSE 80

COPY ./target/fima-transaction-api-17.12.1-SNAPSHOT.jar fima.jar

ENTRYPOINT ["java", "-jar", "fima.jar", "--transaction.service.host=transaction-service", "--transaction.service.port=9997", "--server.port=80"]
