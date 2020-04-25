FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} mipthack.jar
CMD java $JAVA_OPTIONS -jar mipthack.jar --spring.profiles.active=prod