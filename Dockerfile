FROM openjdk:19-jdk-slim
WORKDIR /app
COPY . /app
RUN ./gradlew :bootJar
EXPOSE 8080
ENTRYPOINT java $JAVA_OPTS -jar build/libs/icebreaker-0.0.1-SNAPSHOT.jar
