package br.com.zup.edu.shared.handler

import br.com.zup.edu.shared.handler.exceptions.AutorExistenteException
import br.com.zup.edu.shared.handler.exceptions.CategoriaExistenteException
import br.com.zup.edu.shared.handler.exceptions.LivroExistenteException
import br.com.zup.edu.shared.handler.exceptions.LivroInexistenteException
import com.google.rpc.BadRequest
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerInterceptor: MethodInterceptor<Any, Any?> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<Any, Any?>): Any? {
        return try{
            context.proceed()
        } catch (e: Exception) {
            logger.error(e.message)

            val statusError = when(e) {
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException()
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message).asRuntimeException()
                is ConstraintViolationException -> handleConstraintValidationException(e)
                is AutorExistenteException -> Status.ALREADY_EXISTS.withDescription(e.message).asRuntimeException()
                is CategoriaExistenteException -> Status.ALREADY_EXISTS.withDescription(e.message).asRuntimeException()
                is LivroExistenteException -> Status.ALREADY_EXISTS.withDescription(e.message).asRuntimeException()
                is LivroInexistenteException -> Status.NOT_FOUND.withDescription(e.message).asRuntimeException()
                else -> Status.UNKNOWN.withDescription("Unexpected error happened").asRuntimeException()
            }

            val observer = context.parameterValues[1] as StreamObserver<*>
            observer.onError(statusError)

            null
        }
    }

    private fun handleConstraintValidationException(e: ConstraintViolationException): StatusRuntimeException {
        val badRequest = BadRequest.newBuilder()
            .addAllFieldViolations(e.constraintViolations.map {
                BadRequest.FieldViolation.newBuilder()
                    .setField(it.propertyPath.last().name)
                    .setDescription(it.message)
                    .build()
            }).build()

        val statusProto = com.google.rpc.Status.newBuilder()
            .setCode(Code.INVALID_ARGUMENT_VALUE)
            .setMessage("Request with invalid parameters")
            .addDetails(com.google.protobuf.Any.pack(badRequest))
            .build()

        logger.info("$statusProto")
        return StatusProto.toStatusRuntimeException(statusProto)
    }

}
