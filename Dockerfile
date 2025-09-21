# 빌드 스테이지
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 런타임 스테이지
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar application.jar
COPY src/main/resources/prompt/ /app/prompt/
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx400m", "-Xms256m", "-XX:+UseContainerSupport", "-XX:MaxMetaspaceSize=128m", "-jar", "application.jar"]