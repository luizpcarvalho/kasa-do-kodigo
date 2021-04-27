package br.com.zup.edu.shared

import br.com.zup.edu.CadastraAutorRequest
import br.com.zup.edu.CadastraCategoriaRequest
import br.com.zup.edu.CadastraLivroRequest
import br.com.zup.edu.autor.NovoAutor
import br.com.zup.edu.categoria.NovaCategoria
import br.com.zup.edu.livro.cadastra.NovoLivro
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

fun CadastraLivroRequest.toModel(): NovoLivro {
    return NovoLivro(
        titulo = this.titulo,
        resumo = this.resumo,
        sumario = this.sumario,
        preco = this.preco.toBigDecimalOrNull(),
        paginas = this.paginas,
        isbn = this.isbn,
        dataPublicacao = this.dataPublicacao?.let {
            if (it.isNotBlank()) {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                LocalDate.parse(it, formatter)
            } else {
                null
            }
        },
        categoriaId = this.categoriaId?.toLongOrNull(),
        autorId = this.autorId?.toLongOrNull()
    )
}