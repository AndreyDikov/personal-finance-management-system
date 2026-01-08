FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY gradlew /app/gradlew
COPY gradle /app/gradle
RUN chmod +x /app/gradlew

COPY settings.gradle* /app/
COPY build.gradle* /app/

RUN --mount=type=cache,target=/root/.gradle \
    /app/gradlew --no-daemon -q dependencies

COPY src /app/src

RUN --mount=type=cache,target=/root/.gradle \
    /app/gradlew --no-daemon clean build -x test

RUN set -eux; \
    JAR="$(ls -1 /app/build/libs/*.jar | grep -v 'plain' | head -n 1 || true)"; \
    if [ -z "$JAR" ]; then JAR="$(ls -1 /app/build/libs/*.jar | head -n 1)"; fi; \
    cp "$JAR" /app/app.jar


FROM build AS test
RUN --mount=type=cache,target=/root/.gradle \
    /app/gradlew --no-daemon test


FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
