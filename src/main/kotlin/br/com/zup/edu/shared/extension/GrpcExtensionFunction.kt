package br.com.zup.edu.shared

import br.com.zup.edu.CadastraAutorRequest
import br.com.zup.edu.autor.NovoAutor

fun CadastraAutorRequest.toModel(): NovoAutor {
    return NovoAutor(
        nome = this.nome,
        email = this.email,
        descricao = this.descricao
    )
}