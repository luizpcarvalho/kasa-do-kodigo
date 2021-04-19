package br.com.zup.edu.autor

import br.com.zup.edu.shared.validation.UniqueValue
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class NovoAutor(
    @field:NotBlank
    val nome: String?,

    @field:NotBlank
    @field:Email
    val email: String?,

    @field:NotBlank
    @field:Size(max = 400)
    val descricao: String?
) {
    fun toModel(): Autor {
        return Autor(
            nome = this.nome!!,
            email = this.email!!,
            descricao = this.descricao!!
        )
    }
}