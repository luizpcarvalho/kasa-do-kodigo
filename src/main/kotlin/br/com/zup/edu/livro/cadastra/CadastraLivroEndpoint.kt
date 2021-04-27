package br.com.zup.edu.livro.cadastra

import br.com.zup.edu.*
import br.com.zup.edu.shared.handler.ErrorHandler
import br.com.zup.edu.shared.toModel
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class CadastraLivroEndpoint(@Inject private val service: CadastraLivroService)
    : CadastraLivroServiceGrpc.CadastraLivroServiceImplBase() {

    override fun cadastra(
        request: CadastraLivroRequest,
        responseObserver: StreamObserver<CadastraLivroResponse>
    ) {

        val novoLivro = request.toModel()
        val livroCriado = service.cadastra(novoLivro)

        val response = CadastraLivroResponse.newBuilder()
            .setId(livroCriado.id.toString())
            .setTitulo(livroCriado.titulo)
            .setResumo(livroCriado.resumo)
            .setSumario(livroCriado.sumario)
            .setPreco(livroCriado.preco.toString())
            .setPaginas(livroCriado.paginas)
            .setIsbn(livroCriado.isbn)
            .setDataPublicacao(livroCriado.dataPublicacao.toString())
            .setCategoriaId(livroCriado.categoria.id.toString())
            .setAutorId(livroCriado.autor.id.toString())
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

}