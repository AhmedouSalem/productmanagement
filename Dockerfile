FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/productmanagement*.jar app.jar
COPY otel/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

ENV JAVA_TOOL_OPTIONS="-javaagent:/otel/opentelemetry-javaagent.jar"

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
