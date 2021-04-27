package br.com.zup.edu.categoria

import br.com.zup.edu.livro.Livro
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Categoria(
    @field:NotBlank
    @Column(nullable = false, unique = true)
    val nome: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @OneToMany(mappedBy = "categoria", cascade = [CascadeType.MERGE])
    val livros: MutableList<Livro> = mutableListOf()

    fun associaLivro(livro: Livro) {
        livros.add(livro)
    }
}
