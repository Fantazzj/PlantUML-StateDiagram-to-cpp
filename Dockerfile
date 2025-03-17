FROM gradle:jdk21-alpine AS env
LABEL authors="Fantazzj"

FROM env AS build
LABEL authors="Fantazzj"
WORKDIR /tmp/src
COPY . .
RUN gradle build
WORKDIR /app
RUN tar -C . -xf /tmp/src/build/distributions/plantuml-statediagram-to-cpp-0.1.tar plantuml-statediagram-to-cpp-0.1/lib/

FROM build AS run
LABEL authors="Fantazzj"
WORKDIR /app/plantuml-statediagram-to-cpp-0.1/lib
ENTRYPOINT ["java", "-jar", "plantuml-statediagram-to-cpp-0.1.jar"]
