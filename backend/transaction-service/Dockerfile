FROM adoptopenjdk/openjdk15:jre15u-debianslim-nightly

RUN apt-get update && \
    apt-get install -y \
    nmap \
    curl \
    iputils-ping \
    telnet

COPY ./build/libs/transaction-service-all.jar fima.jar

EXPOSE 9997
EXPOSE 9998

ENTRYPOINT ["java", "-cp", "fima.jar", "fima.services.MainKt"]
