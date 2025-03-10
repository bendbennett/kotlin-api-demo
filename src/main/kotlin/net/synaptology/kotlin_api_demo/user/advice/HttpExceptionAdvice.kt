package net.synaptology.kotlin_api_demo.user.advice

import jakarta.servlet.http.HttpServletRequest
import java.time.LocalDateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice(annotations = [RestController::class])
class HttpExceptionAdvice {
    val logger: Logger = LoggerFactory.getLogger(HttpExceptionAdvice::class.java)

    @ExceptionHandler
    fun handleHttpMessageNotReadableException(
        req: HttpServletRequest,
        e: HttpMessageNotReadableException
    ): ResponseEntity<ErrorMessageModel> {
        logger.atError().log(e.message, e)

        val errorMessage = ErrorMessageModel(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.toString(),
            path = req.requestURI,
            message = e.message
        )

        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler
    fun handleHttpMediaTypeNotSupportedException(
        req: HttpServletRequest,
        e: HttpMediaTypeNotSupportedException
    ): ResponseEntity<ErrorMessageModel> {
        logger.atError().log(e.message, e)

        val errorMessage = ErrorMessageModel(
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
            error = HttpStatus.UNSUPPORTED_MEDIA_TYPE.toString(),
            path = req.requestURI,
            message = e.message
        )

        return ResponseEntity(errorMessage, HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }

    @ExceptionHandler
    fun handleException(
        req: HttpServletRequest,
        e: Exception
    ): ResponseEntity<ErrorMessageModel> {
        logger.atError().log(e.message, e)

        val errorMessage = ErrorMessageModel(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.toString(),
            path = req.requestURI,
            message = e.message
        )

        return ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

class ErrorMessageModel(
    var timestamp: LocalDateTime = LocalDateTime.now(),
    var status: Int? = null,
    var error: String? = null,
    var path: String? = null,
    var message: String? = null
)