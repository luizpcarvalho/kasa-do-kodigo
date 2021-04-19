package br.com.zup.edu.categoria

import br.com.zup.edu.CadastraCategoriaRequest
import br.com.zup.edu.CadastraCategoriaServiceGrpc
import com.google.rpc.BadRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CadastraCategoriaEndpointTest(
    @Inject val repository: CategoriaRepository,
    @Inject val grpcClient: CadastraCategoriaServiceGrpc.CadastraCategoriaServiceBlockingStub
) {

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma nova categoria`() {
        // ação
        val response = grpcClient.cadastra(CadastraCategoriaRequest.newBuilder()
            .setNome("Backend")
            .build())

        // validação
        with(response) {
            assertNotNull(this.id)
            assertEquals("Backend", this.nome)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova categoria quando categoria existente`() {
        // cenário
        repository.save(Categoria("Backend"))

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraCategoriaRequest.newBuilder()
                .setNome("Backend")
                .build())
        }

        // validação
        with(thrown) {
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Categoria existente!", this.status.description)
        }
    }

    @Test
    fun `nao deve cadastrar nova categoria quando o parametro for nulo ou em branco`() {
        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraCategoriaRequest.newBuilder().build())
        }

        // validação
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Request with invalid parameters", this.status.description)
            assertThat(violations(this), containsInAnyOrder(Pair("nome", "must not be blank")))
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : CadastraCategoriaServiceGrpc.CadastraCategoriaServiceBlockingStub {
            return CadastraCategoriaServiceGrpc.newBlockingStub(channel)
        }
    }

    // extrai os detalhes de dentro do erro
    fun violations(e: StatusRuntimeException): List<Pair<String, String>> {

        val details = StatusProto.fromThrowable(e)
            ?.detailsList?.get(0)!!
            .unpack(BadRequest::class.java)

        return details.fieldViolationsList.map { Pair(it.field, it.description) }

    }

}