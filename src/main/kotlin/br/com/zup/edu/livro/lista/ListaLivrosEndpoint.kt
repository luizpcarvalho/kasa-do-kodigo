package br.com.zup.edu.livro.lista

import br.com.zup.edu.*
import br.com.zup.edu.livro.LivroRepository
import br.com.zup.edu.shared.handler.ErrorHandler
import br.com.zup.edu.shared.handler.exceptions.LivroInexistenteException
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ListaLivrosEndpoint(@Inject private val repository: LivroRepository)
    : ConsultaLivrosServiceGrpc.ConsultaLivrosServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun lista(
        request: ListaLivrosRequest,
        responseObserver: StreamObserver<ListaLivrosResponse>
    ) {

        logger.info("Listando os livros cadastrados...")

        val livros = repository.findAll().map {
            ListaLivrosResponse.Livro.newBuilder().setId(it.id.toString()).setTitulo(it.titulo).build()
        }
        val response = ListaLivrosResponse.newBuilder().addAllLivros(livros).build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()

        logger.info("listagem realizada com sucesso")
    }

    override fun detalhe(
        request: DetalheLivroRequest,
        responseObserver: StreamObserver<DetalheLivroResponse>
    ) {

        logger.info("Consultando detalhes do livro com id: ${request.id}")

        val livro = repository.findById(request.id.toLong())

        if(livro.isEmpty) {
            throw LivroInexistenteException("Livro com id: ${request.id} nao encontrado")
        }

        val response = DetalheLivroResponse.newBuilder()
            .setId(livro.get().id.toString())
            .setTitulo(livro.get().titulo)
            .setResumo(livro.get().resumo)
            .setSumario(livro.get().sumario)
            .setPreco(livro.get().preco.toString())
            .setPaginas(livro.get().paginas)
            .setIsbn(livro.get().isbn)
            .setDataPublicacao(livro.get().dataPublicacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            .setCategoria(livro.get().categoria.nome)
            .setAutor(livro.get().autor.nome)
            .setDescricaoAutor(livro.get().autor.descricao)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()

        logger.info("Consulta de detalhes do livro com id: ${request.id} realizada com sucesso")
    }

}