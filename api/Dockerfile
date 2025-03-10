FROM eclipse-temurin:21.0.6_7-jre-alpine@sha256:4c07db858c3b8bfed4cb9163f4aeedbff9c0c2b8212ec1fa75c13a169dec8dc6

ENV USER_NAME api-user
ENV APP_HOME /opt/$USER_NAME/app
ENV SPRING_PROFILES_ACTIVE=default

# we can use -XX:MaxRAMPercentage=85 if we control the memory from the container engine
ENV JVM_HEAP_SETTINGS="-Xmx256m"

ENV JAVA_RUN_OPTS="-Dfile.encoding=UTF-8 \
-Djava.net.preferIPv4Stack=true \
-XX:+UseParallelGC \
-XshowSettings:all \
-XX:+PrintFlagsFinal"

# This variable can be used in runtime to set or override JVM settings. For example:
# docker run --rm -ti -e JAVA_OPTS="-Xms50M -Xmx50M" my-app-image
ENV JAVA_OPTS=""

# Add a volume pointing to /tmp, used by Tomcat
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Create the user that will run the application
RUN adduser -D --shell /bin/bash --home /opt/$USER_NAME $USER_NAME
RUN mkdir -p $APP_HOME

# Add the application's jar to the container
ADD target/*.jar $APP_HOME/application.jar
RUN chown -R $USER_NAME: $APP_HOME

USER $USER_NAME
WORKDIR $APP_HOME

# Run the jar file. Keep $JAVA_OPTS as the last option before -jar
CMD java -Djava.security.egd=file:/dev/./urandom $JVM_HEAP_SETTINGS $JAVA_RUN_OPTS $JAVA_OPTS -jar application.jar
