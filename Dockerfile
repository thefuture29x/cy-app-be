FROM openjdk:11.0.6-jdk
WORKDIR app
COPY build/libs/cy-app-be-0.0.1-SNAPSHOT.jar /app/cy-app-be-0.0.1-SNAPSHOT.jar
EXPOSE 8085
CMD ["java", "-jar", "/app/cy-app-be-0.0.1-SNAPSHOT.jar", "--spring.config.location=../application.properties"]