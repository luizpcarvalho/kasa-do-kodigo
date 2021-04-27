package br.com.zup.edu.livro

import br.com.zup.edu.autor.Autor
import br.com.zup.edu.categoria.Categoria
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.*

@Entity
class Livro(
    @Column(nullable = false, unique = true)
    val titulo: String,
    @Column(nullable = false)
    val resumo: String,
    @Column(nullable = false)
    val sumario: String,
    @Column(nullable = false)
    val preco: BigDecimal,
    @Column(nullable = false)
    val paginas: Int,
    @Column(nullable = false, unique = true)
    val isbn: String,
    @Column(nullable = false)
    val dataPublicacao: LocalDate,
    @ManyToOne
    val categoria: Categoria,
    @ManyToOne
    val autor: Autor
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}