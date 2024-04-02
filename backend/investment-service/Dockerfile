FROM adoptopenjdk/openjdk15:jre15u-debianslim-nightly

RUN apt-get update && \
    apt-get install -y \
    nmap \
    curl \
    iputils-ping \
    telnet

COPY ./target/scala-3.3.3/investment-service-assembly-0.1.0.jar fima.jar

EXPOSE 9997
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "fima.jar"]
