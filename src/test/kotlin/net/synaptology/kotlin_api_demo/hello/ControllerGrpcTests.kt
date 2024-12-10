package net.synaptology.kotlin_api_demo.hello

import io.grpc.ManagedChannel
import io.grpc.inprocess.InProcessChannelBuilder
import kotlinx.coroutines.runBlocking
import net.devh.boot.grpc.client.inject.GrpcClient
import net.synaptology.kotlin_api_demo.proto.HelloRequest
import net.synaptology.kotlin_api_demo.proto.HelloServiceGrpcKt
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import kotlin.test.assertEquals

@SpringBootTest(properties = [
	"grpc.server.inProcessName=test",
	"grpc.server.port=-1",
	"grpc.client.inProcess.address=in-process:test"
])
@SpringJUnitConfig(classes = [ControllerGrpcConfiguration::class])
@DirtiesContext
class ControllerGrpcTests {

	private final val channel: ManagedChannel = InProcessChannelBuilder
		.forName("test")
		.directExecutor()
		.build()

	@GrpcClient("inProcess")
	val service = HelloServiceGrpcKt.HelloServiceCoroutineStub(channel)

	@Test
	@DirtiesContext
	fun hello() = runBlocking {
		val helloRequest = HelloRequest
			.newBuilder()
			.setName("Johnny")
			.build()
		val resp = service.hello(helloRequest)

		assertEquals("Hello, Johnny!", resp.message)
	}

}
