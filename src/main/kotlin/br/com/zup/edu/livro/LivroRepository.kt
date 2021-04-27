package br.com.zup.edu.livro

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface LivroRepository: JpaRepository<Livro, Long> {
    fun existsByTitulo(titulo: String): Boolean
    fun existsByIsbn(isbn: String): Boolean
}