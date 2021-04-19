package br.com.zup.edu.categoria

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class NovaCategoria(
    @field:NotBlank
    val nome: String
) {
    fun toModel(): Categoria {
        return Categoria(nome = nome)
    }
}