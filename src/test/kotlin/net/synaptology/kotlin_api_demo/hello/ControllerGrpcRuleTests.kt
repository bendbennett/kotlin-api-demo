package net.synaptology.kotlin_api_demo.hello

// Use GrpcCleanupRule, see https://grpc.github.io/grpc-java/javadoc/io/grpc/testing/GrpcServerRule.html
import io.grpc.testing.GrpcServerRule
import kotlinx.coroutines.runBlocking
import net.synaptology.kotlin_api_demo.proto.HelloRequest
import net.synaptology.kotlin_api_demo.proto.HelloServiceGrpcKt
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ControllerGrpcRuleTests {

    @get:Rule
    val grpcServerRule: GrpcServerRule = GrpcServerRule().directExecutor()

    @Test
    fun hello() = runBlocking {
        val helloRequest = HelloRequest.newBuilder().setName("Johnny").build()
        val service = ControllerGrpc()

        grpcServerRule.serviceRegistry.addService(service)

        val stub = HelloServiceGrpcKt.HelloServiceCoroutineStub(grpcServerRule.channel)
        assertEquals("Hello, Johnny!", stub.hello(helloRequest).message)
    }
}
