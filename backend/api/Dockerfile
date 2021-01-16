FROM adoptopenjdk/openjdk15:jre15u-debianslim-nightly

EXPOSE 80

COPY ./build/libs/api-all.jar fima.jar

ENTRYPOINT ["java", "-jar", "fima.jar", "--spring.profiles.active=production", "--server.port=8080"]
