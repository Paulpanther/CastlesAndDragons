ARG VERSION=8u151
ARG PORT=5678

FROM openjdk:${VERSION}-jdk as BUILD

COPY . /server
WORKDIR /server
RUN ./gradlew --no-daemon shadowJar
EXPOSE ${PORT}
#CMD tail -f /dev/null
FROM openjdk:${VERSION}-jre

COPY --from=BUILD /server/build/libs/server.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar", "--port", "${PORT}"]
