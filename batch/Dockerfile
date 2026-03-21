FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /opt/build

COPY gradlew gradlew.bat* ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts ./
COPY api/build.gradle.kts ./api/
COPY batch/build.gradle.kts ./batch/
COPY component-library/build.gradle.kts ./component-library/
COPY domain/build.gradle.kts ./domain/
COPY frontend/build.gradle.kts ./frontend/
COPY openapi/build.gradle.kts ./openapi/
COPY utils/build.gradle.kts ./utils/

RUN chmod +x gradlew && ./gradlew :batch:dependencies --no-daemon 2>/dev/null || true

COPY . .

RUN chmod +x gradlew && ./gradlew :batch:bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine

LABEL org.opencontainers.image.title="demo-batch" \
      org.opencontainers.image.description="Batch jobs for demo project" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.vendor="flavien.io" \
      org.opencontainers.image.maintainer="Flavien PERIER <perier@flavien.io>" \
      org.opencontainers.image.url="https://github.com/flavien-perier/spring-boilerplate" \
      org.opencontainers.image.source="https://github.com/flavien-perier/spring-boilerplate" \
      org.opencontainers.image.licenses="MIT"

ARG DOCKER_UID="1000"
ARG DOCKER_GID="1000"

ENV POSTGRES_URL="postgresql://127.0.0.1:5432/admin" \
    POSTGRES_USER="admin" \
    POSTGRES_PASSWORD="password" \
    VALKEY_HOST="127.0.0.1" \
    VALKEY_PORT="6379" \
    VALKEY_PASSWORD="password" \
    SMTP_HOST="127.0.0.1" \
    SMTP_PORT="25" \
    SMTP_USERNAME="" \
    SMTP_PASSWORD="" \
    SMTP_AUTH="no" \
    SMTP_STARTTLS="no" \
    MAIL_ACCOUNT_CREATOR="no-reply@flavien.io" \
    MAIL_DOMAIN_LINKS="https://flavien.io:443"

RUN addgroup -g $DOCKER_GID demo && \
    adduser -G demo -D -H -h /opt/demo -u $DOCKER_UID demo

WORKDIR /opt/demo

COPY --from=builder --chown=demo:demo /opt/build/batch/build/libs/batch-*.jar app.jar

USER demo

CMD ["java", "-jar", "app.jar"]
