# web-project

## Table of contents
* [General info](#general-info)
* [Requirements](#requirements)
* [Startup instructions](#startup-instructions)
* [Usage](#usage)

## General info
Application created for university classes about web applications.
It's a simple application, file hosting or cloud drive wannabe.

Authorization via JWT tokens. Files encrypted with AES algorithm.

Uploaded files will appear in "storage" catalog.
Every user has it's own catalog named by their UUID assigned to them after registration.
Every file is encrypted with different random key. 
The key for decrypting the file is saved in the database encrypted with master key which is known by the backend application.
For simplicity, in this project, master key is saved in application.properties file.

## Requirements
__Docker installation__:
- Docker Engine[^1]

__Manual installation__:
- Postgres database
- Java 11, Maven
- Angular, ng

## Startup instructions

### Docker installation

* Starting the project

    ```docker compose up```

* Stopping containers (not deleting images and volumes)

    ```docker compose down```

* Clean up (deleting images and volumes)

    ```docker compose down --rmi local -v```

### Manual installation

* Update postgres database location and credentials in backend-app/profiles/local.properties file.

* Clear database and init structures from scripts, build the app and run it. Execute these commands in backend-app catalog:

    ```mvn resources:resources liquibase:dropAll -P local```

    ```mvn resources:resources liquibase:update -P local```

    ```mvn clean package -DskipTests -P local```

    ```mvn spring-boot:run -P local```


* Start angular application in frontend-app catalog:

   ```npm install```

   ```ng serve```

## Usage

After installation, application will be available at

> [localhost:4200](http://localhost:4200).


[^1]:[Docker Engine](https://docs.docker.com/engine/install/)
