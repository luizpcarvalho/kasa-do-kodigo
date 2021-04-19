package br.com.zup.edu.shared

import br.com.zup.edu.CadastraAutorRequest
import br.com.zup.edu.CadastraCategoriaRequest
import br.com.zup.edu.autor.NovoAutor
import br.com.zup.edu.categoria.NovaCategoria

fun CadastraAutorRequest.toModel(): NovoAutor {
    return NovoAutor(
        nome = this.nome,
        email = this.email,
        descricao = this.descricao
    )
}

fun CadastraCategoriaRequest.toModel(): NovaCategoria {
    return NovaCategoria(
        nome = this.nome
    )
}