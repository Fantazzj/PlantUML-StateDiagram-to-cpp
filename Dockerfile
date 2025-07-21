FROM eclipse-temurin:17-alpine AS env
LABEL authors="Fantazzj"

FROM env AS build
LABEL authors="Fantazzj"
WORKDIR /app
COPY . .
RUN ./gradlew createReleaseDocker

FROM alpine AS run
LABEL authors="Fantazzj"
COPY --from=build /app/build/releases/docker /app
WORKDIR /app
ENTRYPOINT ["./jre/bin/java", "-jar", "plantuml-statediagram-to-cpp-0.1.jar"]
