FROM adoptopenjdk/openjdk15:jre15u-debianslim-nightly

EXPOSE 80

COPY ./target/api-1.0.0-SNAPSHOT.jar fima.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=production", "-jar", "fima.jar", "--server.port=8080", "--spring.webflux.base-path=/transaction", ]
