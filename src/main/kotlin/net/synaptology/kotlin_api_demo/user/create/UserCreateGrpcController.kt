package net.synaptology.kotlin_api_demo.user.create

import net.devh.boot.grpc.server.service.GrpcService
import net.synaptology.kotlin_api_demo.proto.user.UserCreateRequest
import net.synaptology.kotlin_api_demo.proto.user.UserCreateResponse
import net.synaptology.kotlin_api_demo.proto.user.UserCreateServiceGrpcKt

@GrpcService
class UserCreateGrpcController(val service: IUserCreateService) :
    UserCreateServiceGrpcKt.UserCreateServiceCoroutineImplBase() {
    override suspend fun create(request: UserCreateRequest): UserCreateResponse {

        val inputData = UserCreateInputData(
            request.firstName,
            request.lastName
        )

        val user = service.create(inputData)

        val outputData = UserCreateOutputData(
            user.firstName,
            user.lastName,
            user.createdAt,
            user.id
        )

        return UserCreateResponse.newBuilder()
            .setId(outputData.id)
            .setFirstName(outputData.firstName)
            .setLastName(outputData.lastName)
            .setCreatedAt(outputData.createdAt)
            .build()
    }
}
