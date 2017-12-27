FROM frolvlad/alpine-oraclejdk8:full

EXPOSE 8890

COPY ./target/fima-web-frontend-1705.1-SNAPSHOT-jar-with-dependencies.jar fima.jar

ENTRYPOINT ["java", "-cp", "fima.jar", "fima.frontend.WebFrontend"]
