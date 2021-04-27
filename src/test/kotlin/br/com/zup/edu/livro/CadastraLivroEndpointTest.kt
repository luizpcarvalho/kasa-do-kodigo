package br.com.zup.edu.livro

import br.com.zup.edu.CadastraLivroRequest
import br.com.zup.edu.CadastraLivroServiceGrpc
import br.com.zup.edu.autor.Autor
import br.com.zup.edu.autor.AutorRepository
import br.com.zup.edu.categoria.Categoria
import br.com.zup.edu.categoria.CategoriaRepository
import com.google.rpc.BadRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CadastraLivroEndpointTest(
    @Inject private val repository: LivroRepository,
    @Inject private val autorRepository: AutorRepository,
    @Inject private val categoriaRepository: CategoriaRepository,
    @Inject private val grpcClient: CadastraLivroServiceGrpc.CadastraLivroServiceBlockingStub
) {

    companion object {
        lateinit var autor: Autor
        lateinit var categoria: Categoria
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
        autorRepository.deleteAll()
        categoriaRepository.deleteAll()
        autor = autorRepository.save(Autor("Luiz", "luiz@gmail.com", "Backend Developer"))
        categoria = categoriaRepository.save(Categoria("Backend"))
    }

    @Test
    fun `deve cadastrar novo livro`() {
        // cenário

        // ação
        val response = grpcClient.cadastra(CadastraLivroRequest.newBuilder()
            .setTitulo("Java")
            .setResumo("Resumo")
            .setSumario("Sumario")
            .setPreco("50")
            .setPaginas(200)
            .setIsbn("ABC123")
            .setDataPublicacao("30/05/2080")
            .setCategoriaId(categoria.id.toString())
            .setAutorId(autor.id.toString())
            .build())

        // validação
        with(response) {
            assertNotNull(this.id)
            assertEquals("Java", this.titulo)
            assertEquals("Resumo", this.resumo)
            assertEquals("Sumario", this.sumario)
            assertEquals("50", this.preco)
            assertEquals(200, this.paginas)
            assertEquals("ABC123", this.isbn)
            assertEquals("2080-05-30", this.dataPublicacao)
            assertEquals(categoria.id.toString(), this.categoriaId)
            assertEquals(autor.id.toString(), this.autorId)
        }
    }

    @Test
    fun `nao deve cadastrar novo livro quando titulo existente`() {
        // cenário
        repository.save(Livro("Java", "Resumo", "Sumario", BigDecimal.valueOf(50), 200,
            "ABC321", LocalDate.of(2021,5,30), categoria, autor))

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraLivroRequest.newBuilder()
                .setTitulo("Java")
                .setResumo("Resumo")
                .setSumario("Sumario")
                .setPreco("50")
                .setPaginas(200)
                .setIsbn("ABC123")
                .setDataPublicacao("30/05/2080")
                .setCategoriaId(categoria.id.toString())
                .setAutorId(autor.id.toString())
                .build())
        }

        with(thrown) {
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Livro existente", this.status.description)
        }
    }

    @Test
    fun `nao deve cadastrar novo livro quando isbn existente`() {
        // cenário
        repository.save(Livro("Kotlin", "Resumo", "Sumario", BigDecimal.valueOf(50), 200,
            "ABC123", LocalDate.of(2021,5,30), categoria, autor))

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraLivroRequest.newBuilder()
                .setTitulo("Java")
                .setResumo("Resumo")
                .setSumario("Sumario")
                .setPreco("50")
                .setPaginas(200)
                .setIsbn("ABC123")
                .setDataPublicacao("30/05/2080")
                .setCategoriaId(categoria.id.toString())
                .setAutorId(autor.id.toString())
                .build())
        }

        with(thrown) {
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Livro existente", this.status.description)
        }
    }

    @Test
    fun `nao deve cadastrar novo livro quando resumo maior que 500 caracteres`() {
        // cenário

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraLivroRequest.newBuilder()
                .setTitulo("Java")
                .setResumo("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                .setSumario("Sumario")
                .setPreco("50")
                .setPaginas(200)
                .setIsbn("ABC123")
                .setDataPublicacao("30/05/2080")
                .setCategoriaId(categoria.id.toString())
                .setAutorId(autor.id.toString())
                .build())
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertThat(violations(this), containsInAnyOrder(Pair("resumo", "size must be between 0 and 500")))
        }
    }

    @Test
    fun `nao deve cadastrar novo livro quando preco menor que 20`() {
        // cenário

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraLivroRequest.newBuilder()
                .setTitulo("Java")
                .setResumo("Resumo")
                .setSumario("Sumario")
                .setPreco("10")
                .setPaginas(200)
                .setIsbn("ABC123")
                .setDataPublicacao("30/05/2080")
                .setCategoriaId(categoria.id.toString())
                .setAutorId(autor.id.toString())
                .build())
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertThat(violations(this), containsInAnyOrder(Pair("preco", "must be greater than or equal to 20")))
        }
    }

    @Test
    fun `nao deve cadastrar novo livro quando numero de paginas menor que 100`() {
        // cenário

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraLivroRequest.newBuilder()
                .setTitulo("Java")
                .setResumo("Resumo")
                .setSumario("Sumario")
                .setPreco("50")
                .setPaginas(20)
                .setIsbn("ABC123")
                .setDataPublicacao("30/05/2080")
                .setCategoriaId(categoria.id.toString())
                .setAutorId(autor.id.toString())
                .build())
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertThat(violations(this), containsInAnyOrder(Pair("paginas", "must be greater than or equal to 100")))
        }
    }

    @Test
    fun `nao deve cadastrar novo livro quando data de publicacao nao for no futuro`() {
        // cenário

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraLivroRequest.newBuilder()
                .setTitulo("Java")
                .setResumo("Resumo")
                .setSumario("Sumario")
                .setPreco("50")
                .setPaginas(200)
                .setIsbn("ABC123")
                .setDataPublicacao("30/05/2019")
                .setCategoriaId(categoria.id.toString())
                .setAutorId(autor.id.toString())
                .build())
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertThat(violations(this), containsInAnyOrder(Pair("dataPublicacao", "must be a future date")))
        }
    }

    @Test
    fun `nao deve cadastrar novo livro quando categoria nao existir`() {
        // cenário

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraLivroRequest.newBuilder()
                .setTitulo("Java")
                .setResumo("Resumo")
                .setSumario("Sumario")
                .setPreco("50")
                .setPaginas(200)
                .setIsbn("ABC123")
                .setDataPublicacao("30/05/2080")
                .setCategoriaId("0")
                .setAutorId(autor.id.toString())
                .build())
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertThat(violations(this), containsInAnyOrder(Pair("categoriaId", "Categoria nao existe")))
        }
    }

    @Test
    fun `nao deve cadastrar novo livro quando autor nao existir`() {
        // cenário

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraLivroRequest.newBuilder()
                .setTitulo("Java")
                .setResumo("Resumo")
                .setSumario("Sumario")
                .setPreco("50")
                .setPaginas(200)
                .setIsbn("ABC123")
                .setDataPublicacao("30/05/2080")
                .setCategoriaId(categoria.id.toString())
                .setAutorId("0")
                .build())
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertThat(violations(this), containsInAnyOrder(Pair("autorId", "Autor nao existe")))
        }
    }

    @Test
    fun `nao deve cadastrar novo livro quando parametros nao informados`() {
        // cenário

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraLivroRequest.newBuilder().build())
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Request with invalid parameters", this.status.description)
            assertThat(violations(this), containsInAnyOrder(
                Pair("titulo", "must not be blank"),
                Pair("categoriaId", "must not be null"),
                Pair("autorId", "must not be null"),
                Pair("resumo", "must not be blank"),
                Pair("dataPublicacao", "must not be null"),
                Pair("sumario", "must not be blank"),
                Pair("isbn", "must not be blank"),
                Pair("paginas", "must be greater than or equal to 100"),
            ))
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : CadastraLivroServiceGrpc.CadastraLivroServiceBlockingStub {
            return CadastraLivroServiceGrpc.newBlockingStub(channel)
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