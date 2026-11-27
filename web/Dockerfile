FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM tomcat:10.1-jdk21
# Endereço da API visto de dentro da rede do Docker (servlets: a API roda em outro container) e pelo navegador (página inicial)
ENV API_URL=http://host.docker.internal:8080/api
ENV API_URL_PUBLICA=http://localhost:8080/api
COPY --from=build /app/target/mvc.war /usr/local/tomcat/webapps/mvc.war
EXPOSE 8080
