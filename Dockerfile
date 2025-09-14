# ---------- BUILD STAGE (uses preinstalled Gradle) ----------
FROM gradle:8.14.3-jdk21 AS build
WORKDIR /home/gradle/app

# Build config first (cache)
COPY --chown=gradle:gradle settings.gradle build.gradle ./
COPY --chown=gradle:gradle config ./config

# Warm dependencies (skip checkstyle)
RUN gradle --no-daemon -x checkstyleMain -x checkstyleTest dependencies

# App sources
COPY --chown=gradle:gradle src ./src

# Build jar (skip checkstyle)
RUN gradle clean bootJar --no-daemon -x checkstyleMain -x checkstyleTest

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/app/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
