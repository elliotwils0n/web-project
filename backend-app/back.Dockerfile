FROM maven:3.8.6-jdk-11-slim

RUN useradd -ms /bin/bash app && \
     mkdir -p /home/app/web-project /home/app/storage && \
     chown -R app:app /home/app

ENV MAVEN_CONFIG=/home/app/.m2

WORKDIR /home/app/web-project

COPY --chown=app:app . .

USER app

RUN mvn clean package -DskipTests -P docker

CMD mvn resources:resources liquibase:update -P docker && mvn spring-boot:run -P docker
