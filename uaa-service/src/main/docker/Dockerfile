FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
RUN sh -c 'touch /app.jar'

ENV JAVA_OPTS=""
ENV SPRING_PROFILE="default"


ENTRYPOINT exec java $JAVA_OPTS \
 -Djava.security.egd=file:/dev/./urandom \
 -Dspring.profiles.active=$SPRING_PROFILE \
 -jar app.jar
