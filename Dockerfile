FROM docker.io/eclipse-temurin:21-jre-jammy

RUN apt-get update && \
    apt-get install -y --no-install-recommends qpdf && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/pdf-processor-*.jar /app/pdf-processor.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/pdf-processor.jar"]