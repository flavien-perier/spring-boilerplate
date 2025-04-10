FROM alpine:3.20

LABEL org.opencontainers.image.title="demo" \
      org.opencontainers.image.description="Demo project with authentication" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.vendor="flavien.io" \
      org.opencontainers.image.maintainer="Flavien PERIER <perier@flavien.io>" \
      org.opencontainers.image.url="https://github.com/flavien-perier/spring-boilerplate" \
      org.opencontainers.image.source="https://github.com/flavien-perier/spring-boilerplate" \
      org.opencontainers.image.licenses="MIT"

ARG DOCKER_UID="1000" \
    DOCKER_GID="1000"

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

COPY --chown=demo:demo --chmod=550 ./target/demo ./demo
COPY --chown=demo:demo --chmod=440 ./target/*.so ./

USER demo

EXPOSE 8080

CMD ./demo