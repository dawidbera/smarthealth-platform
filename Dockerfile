FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
# JVM-level memory limits
ENTRYPOINT ["java", "-Xmx192m", "-Xms128m", "-jar", "app.jar"]
