FROM eclipse-temurin:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILES=build/libs/\*.jar
ARG PROPERTIES_FILE=build/resources/main/application.properties
ARG CREDS_FILE=src/main/resources/web-based-gtfs-validator-a088ec5f045d.json
ARG CURRENT_VERSION=4.0.1-SNAPSHOT
ENV CURRENT_VERSION=${CURRENT_VERSION}
COPY ${JAR_FILES} /
COPY ${PROPERTIES_FILE} /
COPY ${CREDS_FILE} /
ENTRYPOINT exec java -Xmx12g -jar /service-${CURRENT_VERSION}.jar
