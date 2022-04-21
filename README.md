# web-project

## Table of contents
* [General info](#general-info)
* [Requirements](#requirements)
* [Startup instructions](#startup-instructions)
* [Links](#links)

## General info
Application created for university classes about web applications.
It's a simple application, file hosting or cloud drive wannabe.

Authorization via JWT tokens. Files encrypted with RSA algorithm.

Uploaded files will appear in "storage" catalog.
Every user has it's own catalog named by their UUID assigned while to them after registration.
Every file is encrypted with different random key. 
The key is saved in the database encrypted with master key which is known by the backend application.
For simplicity, in this project, master key is saved in application.properties file.

## Requirements
* Docker & Docker Compose or Postgres database
* Java 11, Maven
* Angular, ng

## Startup instructions

* Start Postgres in Docker, run command in backend-app/docker-compose catalog:

   ```sudo docker-compose up```


* Init database structure, build the app and run it. Execute these commands in backend-app catalog:

    ```mvn resources:resources liquibase:dropAll -P local```

    ```mvn resources:resources liquibase:update -P local```

    ```mvn clean package -DskipTests -P local```

    ```mvn spring-boot:run -P local```


* Start angular application in frontend-app catalog:

   ```npm install```

   ```ng serve```


* Application will be available at [localhost:4200](http://localhost:4200).

## Links
* [Docker compose](https://docs.docker.com/compose/)
