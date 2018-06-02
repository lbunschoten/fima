FROM frolvlad/alpine-oraclejdk8:full

COPY ./target/fima-transaction-service-server-18.03.1-SNAPSHOT-jar-with-dependencies.jar fima.jar

EXPOSE 9997

ENTRYPOINT ["java", "-cp", "fima.jar", "fima.services.transaction.MainKt"]
