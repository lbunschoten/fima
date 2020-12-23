FROM adoptopenjdk/openjdk15:jre15u-debianslim-nightly

RUN apt-get update && \
    apt-get install -y \
    nmap \
    curl \
    iputils-ping \
    telnet

COPY ./target/transaction-service-1.0.0-SNAPSHOT-jar-with-dependencies.jar fima.jar

EXPOSE 9997

ENTRYPOINT ["java", "-cp", "fima.jar", "fima.services.transaction.MainKt"]
