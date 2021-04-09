package br.com.zup.edu.autor

import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Autor(
    @field:NotBlank
    @Column(nullable = false)
    val nome: String,

    @field:NotBlank
    @Column(nullable = false, unique = true)
    val email: String,

    @field:NotBlank
    @Column(nullable = false)
    val descricao: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false)
    val cadastradoEm = LocalDateTime.now()
}