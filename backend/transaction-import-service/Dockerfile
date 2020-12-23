FROM adoptopenjdk/openjdk15:jre15u-debianslim-nightly

COPY ./target/transaction-import-service-1.0.0-SNAPSHOT-jar-with-dependencies.jar fima.jar

EXPOSE 9997

ENTRYPOINT ["java", "-cp", "fima.jar", "fima.services.transactionimport.MainKt"]
