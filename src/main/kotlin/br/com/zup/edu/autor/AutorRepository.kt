package br.com.zup.edu.autor

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface AutorRepository: JpaRepository<Autor, Long> {
    fun existsByEmail(email: String): Boolean
}