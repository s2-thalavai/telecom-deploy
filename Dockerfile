# ---------- build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY src src
ARG APP_VERSION=1.0.0-SNAPSHOT
RUN mvn -B package -DskipTests -Drevision=${APP_VERSION} \
 && java -Djarmode=layertools -jar target/*.jar extract --destination target/layers

# ---------- runtime stage: same image promoted dev -> qa -> staging -> prod ----------
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /workspace/target/layers/dependencies/ ./
COPY --from=build /workspace/target/layers/spring-boot-loader/ ./
COPY --from=build /workspace/target/layers/snapshot-dependencies/ ./
COPY --from=build /workspace/target/layers/application/ ./
USER app
EXPOSE 8080 9091
# Container-aware heap sizing; environment chosen at RUNTIME, never at build time
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError" \
    SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
