FROM amazoncorretto:17.0.8
ARG APP_VERSION
RUN mkdir /app
COPY target/stockflare-middleware-service-${APP_VERSION}.jar /app/app.jar
COPY ./VERSION.txt /app/VERSION.txt
ENTRYPOINT ["java", "-jar", "/app/app.jar"]