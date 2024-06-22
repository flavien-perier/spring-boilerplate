# Spring boilerplate project

The aim of this project is to provide a code base to facilitate the launch of new projects.

The project already includes an authentication and session/user management system.

The code for the controllers (backend) and the http client (frontend) is generated at compile time based on the contents of the OpenAPI documentation file.

Access rights to the various resources are also managed in the OpenAPI file.

The project is compiled using GraalVM. This significantly reduces the size of the container.

Profile files for optimizing GraalVM compilation are generated using tests. Projects launched with this boilerplate will be faster if more tested.

## Base documentation

[OpenAPI Documentation](src/main/resources/openapi.yaml)

## Technologies used

### Development

- [IntelliJ](https://www.jetbrains.com/idea/)
- [OpenAPI](https://openai.com/)
- [OpenAPI Generator (Spting)](https://openapi-generator.tech/docs/generators/spring)
- [OpenAPI Generator (typescript-axios)](https://openapi-generator.tech/docs/generators/typescript-axios)

### Backend

- [Kotlin](https://kotlinlang.org/)
- [Spring Boot](https://spring.io/projects/spring-boot/)
- [Maven](https://maven.apache.org/)
- [GraalVM](https://www.graalvm.org/)
- [OWASP dependency check](https://owasp.org/www-project-dependency-check/)

### Infra

- [Docker](https://www.docker.com/)
- [Helm](https://helm.sh/)
- [K3s](https://k3s.io/)
- [PostgreSQL](https://www.postgresql.org/)
- [Valkey](https://valkey.io/)

### Frontend

- [Vue3](https://vuejs.org/)
- [Pinia](https://pinia.vuejs.org/)
- [Bootstrap](https://getbootstrap.com/)
- [Font Awesome](https://fontawesome.com/)
- [Undraw](https://undraw.co/)
- [Axios](https://axios-http.com/)

## Build

Build GraalVM :

```bash
mvn -Pnative clean compile spring-boot:process-aot spring-boot:process-test-aot package native:compile
```
