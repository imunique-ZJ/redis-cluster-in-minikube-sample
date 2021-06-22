FROM docker.io/adoptopenjdk/openjdk8:jdk8u292-b10-alpine-slim
COPY build/libs/redis-sample-0.0.1-SNAPSHOT.jar redis-sample-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/redis-sample-0.0.1-SNAPSHOT.jar"]
