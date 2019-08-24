FROM openjdk:10.0.2-jre-slim

EXPOSE 80

COPY ./target/fima-transaction-api-17.12.1-SNAPSHOT.jar fima.jar

ENV TRANSACTION_SERVICE_HOST=transaction-service
ENV TRANSACTION_SERVICE_PORT=9997

ENV TRANSACTION_STATISTICS_SERVICE_HOST=transaction-statistics-service
ENV TRANSACTION_STATISTICS_SERVICE_PORT=9997

ENV TRANSACTION_IMPORT_SERVICE_HOST=transaction-import-service
ENV TRANSACTION_IMPORT_SERVICE_PORT=9997

ENTRYPOINT ["java", "-jar", "fima.jar", "--server.port=8080", "--server.servlet.context-path=/transaction"]
