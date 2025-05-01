FROM eclipse-temurin:17-alpine AS env
LABEL authors="Fantazzj"

FROM env AS build
LABEL authors="Fantazzj"
WORKDIR /app
COPY . .
RUN ./gradlew installDist

FROM eclipse-temurin:17-jre-alpine AS run
LABEL authors="Fantazzj"
COPY --from=build /app/build/install/plantuml-statediagram-to-cpp/lib /app
WORKDIR /app
ENTRYPOINT ["java", "-jar", "plantuml-statediagram-to-cpp-0.1.jar"]
