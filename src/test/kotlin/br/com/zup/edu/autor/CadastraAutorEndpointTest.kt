package br.com.zup.edu.autor

import br.com.zup.edu.CadastraAutorRequest
import br.com.zup.edu.CadastroAutorServiceGrpc
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false) // recomendado para testes com grpc
internal class CadastraAutorEndpointTest(val repository: AutorRepository,
                                         val grpcClient: CadastroAutorServiceGrpc.CadastroAutorServiceBlockingStub) {

    @BeforeEach
    fun setup() {
        // cenário
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar novo autor`() {
        // cenário

        // ação
        val response = grpcClient.cadastra(CadastraAutorRequest.newBuilder()
            .setNome("Luiz")
            .setEmail("luiz.carvalho@zup.com.br")
            .setDescricao("Backend Developer")
            .build())

        // validação
        with(response) {
            assertNotNull(this.id)
            assertNotNull(this.cadastradoEm)
            assertTrue(repository.existsById(this.id.toLong()))
        }
    }

    @Test // @Test é transactional, portanto deve desativar o transactional no @MicronautTest
    fun `nao deve criar proposta quando email existente`() {
        // cenário
        repository.save(Autor(
            nome = "Luiz",
            email = "luiz.carvalho@zup.com.br",
            descricao = "Backend Developer"
        ))

        // ação
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(
                CadastraAutorRequest.newBuilder()
                    .setNome("Paulo")
                    .setEmail("luiz.carvalho@zup.com.br")
                    .setDescricao("Frontend Developer")
                    .build()
            )
        }

        // validação
        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Email existente", this.status.description)
        }
    }

    @Test
    fun `nao deve cadastrar autor quando parametros de entrada forem nulos ou estiverem em branco`() {
        // cenário

        // ação
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraAutorRequest.newBuilder().build())
        }

        // validação
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Request with invalid parameters", this.status.description)
            // verifica cada uma das validações
            assertThat(violations(this), containsInAnyOrder(
                Pair("nome", "não deve estar em branco"),
                Pair("email", "não deve estar em branco"),
                Pair("descricao", "não deve estar em branco")
            ))
        }
    }

    @Test
    fun `nao deve cadastrar autor quando email estiver mal formado e descricao for maior que 400 caracteres`() {
        // cenário

        // ação
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraAutorRequest.newBuilder()
                .setNome("Luiz")
                .setEmail("luiz.carvalhozup.com.br")
                .setDescricao("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                        "\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\"" +
                        "\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\"" +
                        "\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\"" +
                        "\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\"")
                .build())
        }

        // validação
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Request with invalid parameters", this.status.description)
            // verifica cada uma das validações
            assertThat(violations(this), containsInAnyOrder(
                Pair("email", "deve ser um endereço de e-mail bem formado"),
                Pair("descricao", "tamanho deve ser entre 0 e 400")
            ))
        }
    }

    @Factory
    class Clients {
        @Singleton                  // pega endereço e porta
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
        : CadastroAutorServiceGrpc.CadastroAutorServiceBlockingStub {
            return CadastroAutorServiceGrpc.newBlockingStub(channel)
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