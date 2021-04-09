package br.com.zup.edu.autor

import br.com.zup.edu.CadastraAutorRequest
import br.com.zup.edu.CadastraAutorResponse
import br.com.zup.edu.CadastroAutorServiceGrpc
import br.com.zup.edu.shared.extension.toTimestamp
import br.com.zup.edu.shared.handler.ErrorHandler
import br.com.zup.edu.shared.toModel
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class CadastraAutorEndpoint(@Inject private val service: CadastraAutorService)
    : CadastroAutorServiceGrpc.CadastroAutorServiceImplBase() {

    override fun cadastra(request: CadastraAutorRequest, responseObserver: StreamObserver<CadastraAutorResponse>) {

        val novoAutor = request.toModel()
        val autorCriado = service.cadastra(novoAutor)

        val response = CadastraAutorResponse.newBuilder()
            .setId(autorCriado.id.toString())
            .setNome(autorCriado.nome)
            .setEmail(autorCriado.email)
            .setDescricao(autorCriado.descricao)
            .setCadastradoEm(autorCriado.cadastradoEm.toTimestamp())
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

}

