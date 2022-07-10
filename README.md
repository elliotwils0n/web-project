# web-project

## Table of contents
* [General info](#general-info)
* [Requirements](#requirements)
* [Startup instructions](#startup-instructions)
* [Links](#links)

## General info
Application created for university classes about web applications.
It's a simple application, file hosting or cloud drive wannabe.

Authorization via JWT tokens. Files encrypted with AES algorithm.

Uploaded files will appear in "storage" catalog.
Every user has it's own catalog named by their UUID assigned to them after registration.
Every file is encrypted with different random key. 
The key is saved in the database encrypted with master key which is known by the backend application.
For simplicity, in this project, master key is saved in application.properties file.

## Requirements
* Docker Engine (recommended) 
* Postgres database, Java 11, Maven, Angular, ng

## Startup instructions

### Docker

* Starting the project

   ```sudo docker-compose up```

* Clean up (not deleting volumes in case if you would like to run the project multiple times but shutdown for now.).

   ```docker compose down```

* Total clean up (deleting volumes and images)

   ```docker compose down --rmi local -v```

### Manual installation

* Update postgres database location and credentials in profile/local.properties file.

* Clear database and init structure from scripts, build the app and run it. Execute these commands in backend-app catalog:

    ```mvn resources:resources liquibase:dropAll -P local```

    ```mvn resources:resources liquibase:update -P local```

    ```mvn clean package -DskipTests -P local```

    ```mvn spring-boot:run -P local```


* Start angular application in frontend-app catalog:

   ```npm install```

   ```ng serve```


* Application will be available at [localhost:4200](http://localhost:4200).

## Links
* [Docker Engine](https://docs.docker.com/engine/install/)
