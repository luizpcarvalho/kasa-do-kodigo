package br.com.zup.edu.categoria

import br.com.zup.edu.CadastraCategoriaRequest
import br.com.zup.edu.CadastraCategoriaResponse
import br.com.zup.edu.CadastraCategoriaServiceGrpc
import br.com.zup.edu.shared.handler.ErrorHandler
import br.com.zup.edu.shared.toModel
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class CadastraCategoriaEndpoint(@Inject private val service: CadastraCategoriaService)
    : CadastraCategoriaServiceGrpc.CadastraCategoriaServiceImplBase() {

    override fun cadastra(
        request: CadastraCategoriaRequest,
        responseObserver: StreamObserver<CadastraCategoriaResponse>
    ) {

        val novaCategoria = request.toModel()
        val categoriaCriada = service.cadastra(novaCategoria)

        val response = CadastraCategoriaResponse.newBuilder()
            .setId(categoriaCriada.id.toString())
            .setNome(categoriaCriada.nome)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()

    }

}