# hexagonal-architecture

A hexagonal architecture based web application which manages user accounts.


## Requirements

* JDK 17.
* Docker and Docker Compose.

## Run

First, run `docker-compose`:

```sh
# start PostgreSQL database and Kafka broker
docker-compose --file docker/docker-compose.yaml up
```

Then, in another terminal, start the application using the Maven wrapper:

```sh
./mvnw spring-boot:run
```

## Description

This repository contains a user account web application based on principles
behind hexagonal architecture (Cockburn [1]).

The accounts can be queried and manipulated through a RESTful API. See
[doc/openapi.yaml](doc/openapi.yaml) for more details. Accounts can also be deleted through a Kafka message.
See [doc/asyncapi.yaml](doc/asyncapi.yaml) for more details.

## References

[1] Alistair Cockburn. [<cite>Hexagonal architecture</cite>](https://alistair.cockburn.us/hexagonal-architecture/).
