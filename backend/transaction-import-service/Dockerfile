FROM adoptopenjdk/openjdk15:jre15u-debianslim-nightly

COPY ./build/libs/transaction-import-service-all.jar fima.jar

EXPOSE 9997

ENTRYPOINT ["java", "-cp", "fima.jar", "fima.services.transactionimport.MainKt"]
