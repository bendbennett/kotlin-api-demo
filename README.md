# Kotlin API Demo

This repo contains code for an API written in [Kotlin](https://kotlinlang.org/)
with [Spring Boot](https://spring.io/projects/spring-boot).

## Overview

### Tags

| Tag               | Implementation                                                 | 
|-------------------|----------------------------------------------------------------|
| [v0.1.0](#v0.1.0) | Basic HTTP and gRPC server exposing hello world endpoints.     |
| [v0.2.0](#v0.2.0) | Adds HTTP and gRPC endpoints to create users stored in-memory. |
| [v0.3.0](#v0.3.0) | Stores created users in-memory or in PostgreSQL.               |


### Set-up

To run the API you'll need to have [java](https://www.oracle.com/uk/java/technologies/downloads/)
installed.

### Gradle

The [Gradle](https://gradle.org/) build tool can be used for building, running and testing the API.

* `./gradlew bootRun` runs the application without first building an archive.
  * Refer to [Running your Application with Gradle](https://docs.spring.io/spring-boot/gradle-plugin/running.html).
* `./gradle build` executes the build. 
  * The generated .jar can then be run directly using `java -jar build/libs/kotlin_api_demo-<VERSION>.jar` 
* `./gradle test` runs the tests.

### Manual Testing

You can test the API manually using a client. For instance
[Insomnia](https://insomnia.rest/download)
supports both HTTP and [gRPC](https://support.insomnia.rest/article/188-grpc#overview).

Alternatively, requests can be issued using cURL and
[gRPCurl](https://github.com/fullstorydev/grpcurl).

## <a name="v0.3.0"></a>v0.3.0

Stores created users either in-memory or in PostgreSQL.

The same cURL and gRPCurl requests as described for [v0.2.0](#v0.2.0) can be used.

### PostgreSQL

To use [PostgreSQL](https://www.postgresql.org/) storage you'll need to have 
[Docker](https://docs.docker.com/engine/install/) installed.

Edit `application.yml` and ensure that `profiles.active` is set to 
`postgresql`.

```yml
spring:
  profiles.active: postgresql
```

Then run the following command:

```shell
./gradlew composeUp
```

Running the following will terminate and clean-up the PostgreSQL docker 
container:

```shell
./gradlew composeDown
```

### H2

To use in-memory storage ([H2](https://www.h2database.com/html/main.html)),
edit `application.yml` and ensure that `profiles.active` is set to `h2`.

## <a name="v0.2.0"></a>v0.2.0

Adding HTTP and gRPC endpoints for user creation.

Users are stored in-memory.

### HTTP

#### Request

    curl -i --request POST \
    --url http://localhost:8080/user \
    --header 'Content-Type: application/json' \
    --data '{
        "first_name": "john",
        "last_name": "smith"
    }'

##### Response

    HTTP/1.1 201
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Mon, 23 Dec 2024 03:48:32 GMT

    {
        "first_name":"john",
        "last_name":"smith",
        "created_at":"2024-12-23T03:48:32.778473",
        "id":"8cde7c6f-1b37-4666-bda5-c0f446237963"
    }

### gRPC

#### Request

    grpcurl \
    -plaintext \
    -d '{"first_name": "john", "last_name": "smith"}' \
    localhost:8082 net.synaptology.kotlin_api_demo.UserService.Create

#### Response

    {
        "id": "e3f2505c-dbf3-4b87-96e7-2b94cabec5fd",
        "first_name": "john",
        "last_name": "smith",
        "created_at": "2024-12-23T03:53:18.288108"
    }

## <a name="v0.1.0"></a>v0.1.0

Basic HTTP and gRPC server.

### HTTP

#### Request

    curl -i localhost:8080

##### Response

    HTTP/1.1 200
    Content-Length: 0
    Date: Sat, 14 Dec 2024 14:10:26 GMT

### gRPC

#### Request

    grpcurl -plaintext 
    -d '{"name":"world"}' 
    localhost:8082 net.synaptology.kotlin_api_demo.HelloService.Hello

#### Response

    {
      "message": "Hello, world!"
    }
