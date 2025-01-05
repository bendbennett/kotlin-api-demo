package net.synaptology.kotlin_api_demo.user.advice

import io.grpc.Status
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@GrpcAdvice
class GrpcExceptionAdvice {

    val logger: Logger = LoggerFactory.getLogger(GrpcExceptionAdvice::class.java)

    @GrpcExceptionHandler
    fun handleIllegalArgumentException(e: IllegalArgumentException): Status {
        logger.atError().log(e.message, e)
        return Status.INVALID_ARGUMENT.withDescription(e.message).withCause(e)
    }

    @GrpcExceptionHandler
    fun handleException(e: Exception): Status {
        logger.atError().log(e.message, e)
        return Status.INTERNAL.withDescription(e.message).withCause(e)
    }
}
