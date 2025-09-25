FROM eclipse-temurin:21
LABEL org.opencontainers.image.authors="DAW2"
WORKDIR /opt/app
COPY build/libs/*.jar myapp.jar
CMD ["java", "-jar", "myapp.jar"]
