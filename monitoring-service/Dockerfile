FROM openjdk:17-jdk-slim

ENV VERTICLE_FILE monitoring-service-1.0-fat.jar
ENV VERTICLE_HOME /app

# expose the port that verticle listens to in rxListen(port)
EXPOSE 8881
RUN mkdir /app
COPY build/libs/$VERTICLE_FILE $VERTICLE_HOME

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]
