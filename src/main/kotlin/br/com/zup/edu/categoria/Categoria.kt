package br.com.zup.edu.categoria

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Categoria(
    @field:NotBlank
    @Column(nullable = false, unique = true)
    val nome: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
