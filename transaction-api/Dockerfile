FROM frolvlad/alpine-oraclejdk8:full

EXPOSE 80

COPY ./target/transaction-api-17.12.1-SNAPSHOT-jar-with-dependencies.jar fima.jar

ENTRYPOINT ["java", "-cp", "fima.jar", "TransactionApi"]
