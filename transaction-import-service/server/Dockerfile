FROM openjdk:10.0.2-jre-slim

COPY ./target/fima-transaction-import-service-server-18.03.1-SNAPSHOT-jar-with-dependencies.jar fima.jar

EXPOSE 9997

ENTRYPOINT ["java", "-cp", "fima.jar", "fima.services.transactionimport.MainKt"]
