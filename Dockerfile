FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM tomcat:10.1-jdk21
ENV DB_HOST=mysql
COPY --from=build /app/deploy/mvc.war /usr/local/tomcat/webapps/mvc.war
EXPOSE 8080
