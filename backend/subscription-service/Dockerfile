FROM adoptopenjdk/openjdk15:jre15u-debianslim-nightly

RUN apt-get update && \
    apt-get install -y \
    nmap \
    curl \
    iputils-ping \
    telnet

COPY ./target/scala-2.13/subscription-service-assembly-0.1.0.jar fima.jar

EXPOSE 9997

ENTRYPOINT ["java", "-jar", "fima.jar"]
