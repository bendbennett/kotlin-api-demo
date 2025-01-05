package net.synaptology.kotlin_api_demo.user.read

import net.devh.boot.grpc.server.service.GrpcService
import net.synaptology.kotlin_api_demo.proto.user.UserReadRequest
import net.synaptology.kotlin_api_demo.proto.user.UserReadResponse
import net.synaptology.kotlin_api_demo.proto.user.UserReadServiceGrpcKt
import net.synaptology.kotlin_api_demo.proto.user.UsersReadResponse

@GrpcService
class UserReadGrpcController(val service: IUserReadService) :
    UserReadServiceGrpcKt.UserReadServiceCoroutineImplBase() {
    override suspend fun read(request: UserReadRequest): UsersReadResponse {

        val users = service.read()

        val usersIterator = users.iterator()
        val outputData = mutableListOf<UserReadOutputData>()

        while (usersIterator.hasNext()) {
            val user = usersIterator.next()

            val outputDatum = UserReadOutputData(
                user.firstName,
                user.lastName,
                user.createdAt,
                user.id
            )

            outputData.add(outputDatum)
        }

        val outputDataIterator = outputData.iterator()
        val usersReadResponse = mutableListOf<UserReadResponse>()

        while (outputDataIterator.hasNext()) {
            val outputDatum = outputDataIterator.next()

            val userReadResponse = UserReadResponse.newBuilder()
                .setId(outputDatum.id)
                .setFirstName(outputDatum.firstName)
                .setLastName(outputDatum.lastName)
                .setCreatedAt(outputDatum.createdAt)
                .build()


            usersReadResponse.add(userReadResponse)
        }

        return UsersReadResponse.newBuilder()
            .addAllUsers(usersReadResponse)
            .build()
    }
}
