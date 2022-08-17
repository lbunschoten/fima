FROM adoptopenjdk/openjdk15:jre15u-debianslim-nightly

RUN apt-get update && \
    apt-get install -y \
    nmap \
    curl \
    iputils-ping \
    telnet

COPY ./out/main/assembly.dest/out.jar fima.jar

EXPOSE 9997

ENTRYPOINT ["java", "-jar", "fima.jar"]
