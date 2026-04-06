# 1. Base Image
FROM amazoncorretto:17

# 2. Set Working Directory
WORKDIR /app

# 3. Dependency Caching (의존성 캐싱)
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon || true

# 4. Build Application (소스 코드 복사 및 빌드)
COPY . .
RUN ./gradlew clean bootJar --no-daemon

# 5. Expose Port 80
EXPOSE 80

# 6. Runtime Environment Variables
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 7. Entrypoint (Infers JAR name from ENV)
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]