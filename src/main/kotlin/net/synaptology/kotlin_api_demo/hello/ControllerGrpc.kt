package net.synaptology.kotlin_api_demo.hello

import net.devh.boot.grpc.server.service.GrpcService
import net.synaptology.kotlin_api_demo.proto.HelloRequest
import net.synaptology.kotlin_api_demo.proto.HelloResponse
import net.synaptology.kotlin_api_demo.proto.HelloServiceGrpcKt

@GrpcService
class ControllerGrpc: HelloServiceGrpcKt.HelloServiceCoroutineImplBase() {
    override suspend fun hello(request: HelloRequest): HelloResponse {
        return HelloResponse.newBuilder()
            .setMessage("Hello, ${request.name}!")
            .build()
    }
}