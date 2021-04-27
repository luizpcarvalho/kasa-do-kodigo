package br.com.zup.edu.livro.cadastra

import br.com.zup.edu.autor.Autor
import br.com.zup.edu.categoria.Categoria
import br.com.zup.edu.livro.Livro
import br.com.zup.edu.shared.validation.ExistsById
import io.micronaut.core.annotation.Introspected
import java.math.BigDecimal
import java.time.LocalDate
import javax.validation.constraints.*

@Introspected
data class NovoLivro(
    @field:NotBlank
    val titulo: String?,
    @field:NotBlank
    @field:Size(max = 500)
    val resumo: String?,
    @field:NotBlank
    val sumario: String?,
    @field:NotBlank
    @field:DecimalMin("20")
    val preco: BigDecimal?,
    @field:NotNull
    @field:Min(100)
    val paginas: Int?,
    @field:NotBlank
    val isbn: String?,
    @field:NotNull
    @field:Future
    val dataPublicacao: LocalDate?,
    @field:NotNull
    @field:ExistsById(tabela = "Categoria", message = "Categoria nao existe")
    val categoriaId: Long?,
    @field:NotNull
    @field:ExistsById(tabela = "Autor", message = "Autor nao existe")
    val autorId: Long?
) {
    fun toModel(categoria: Categoria, autor: Autor): Livro {
        return Livro(
            titulo = titulo!!,
            resumo = resumo!!,
            sumario = sumario!!,
            preco = preco!!,
            paginas = paginas!!,
            isbn = isbn!!,
            dataPublicacao = dataPublicacao!!,
            categoria = categoria,
            autor = autor
        )
    }
}