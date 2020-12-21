FROM openjdk:10.0.2-jre-slim

COPY ./target/transaction-import-service-1.0.0-SNAPSHOT-jar-with-dependencies.jar fima.jar

EXPOSE 9997

ENTRYPOINT ["java", "-cp", "fima.jar", "fima.services.transactionimport.MainKt"]
