ARG VERSION=8u151
ARG PORT=5678

FROM openjdk:${VERSION}-jdk as BUILD

COPY . /src
WORKDIR /src
RUN ./gradlew --no-daemon shadowJar
EXPOSE ${PORT}

FROM openjdk:${VERSION}-jre

COPY --from=BUILD /src/build/libs/*.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar", "--port", "${PORT}"]