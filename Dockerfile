# ===== Build stage =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# copy wrapper + gradle files first
COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle ./gradle
RUN chmod +x ./gradlew

# (optional) warm up wrapper
RUN ./gradlew --no-daemon -v

# app sources
COPY src ./src

# build the jar (add stacktrace for better logs)
RUN ./gradlew --no-daemon --stacktrace clean bootJar

# ===== Run stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","/app/app.jar"]
