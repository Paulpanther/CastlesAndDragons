ARG VERSION=8u151

FROM openjdk:${VERSION}-jdk as BUILD

COPY . /server
WORKDIR /server
RUN ./gradlew --no-daemon shadowJar

FROM openjdk:${VERSION}-jre
ENV PORT=5678

EXPOSE ${PORT}
COPY --from=BUILD /server/build/libs/server.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar", "--port", "${PORT}"]
