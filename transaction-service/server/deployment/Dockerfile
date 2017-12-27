FROM frolvlad/alpine-oraclejdk8:full

COPY ./target/fima-transaction-service-server-1649.1-SNAPSHOT-jar-with-dependencies.jar fima.jar

EXPOSE 9997

ENTRYPOINT ["java", "-cp", "fima.jar", "fima.transactionservice.TransactionServiceServer"]
