# Kotlin API Demo

This repo contains code for an API written in [Kotlin](https://kotlinlang.org/)
with [Spring Boot](https://spring.io/projects/spring-boot).

## Overview

### Tags

| Tag               | Implementation                                             | 
|-------------------|------------------------------------------------------------|
| [v0.1.0](#v0.1.0) | Basic HTTP and gRPC server exposing hello world endpoints. |

### Set-up

To run the API you'll need to have [java](https://www.oracle.com/uk/java/technologies/downloads/) and
installed.

### Gradle

The [Gradle](https://gradle.org/) build tool can be used for building, running and testing the API.

* `./gradlew bootRun` runs the application without first building an archive (refer
  to [Running your Application with Gradle](https://docs.spring.io/spring-boot/gradle-plugin/running.html)).
* `./gradle build` executes the build. 
  * The generated .jar can then be run directly using `java -jar build/libs/kotlin_api_demo-<VERSION>.jar` 
* `./gradle test` runs the tests.

### Manual Testing

You can test the API manually using a client. For instance
[Insomnia](https://insomnia.rest/download)
supports both HTTP and [gRPC](https://support.insomnia.rest/article/188-grpc#overview).

Alternatively, requests can be issued using cURL and
[gRPCurl](https://github.com/fullstorydev/grpcurl).

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
