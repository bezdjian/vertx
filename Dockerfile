FROM adoptopenjdk:16-jre-hotspot

ENV APP_HOME /app
RUN mkdir $APP_HOME

ENV FAT_JAR vertex-starter-1.0.0-SNAPSHOT-fat.jar

EXPOSE 8888

COPY build/libs/$FAT_JAR $APP_HOME

WORKDIR $APP_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $FAT_JAR"]
