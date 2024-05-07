FROM openjdk:17
VOLUME /tmp
EXPOSE 8005
COPY target/QR-api-0.0.1-SNAPSHOT.jar qr-api.jar
ENTRYPOINT ["java","-jar","/qr-api.jar"]
