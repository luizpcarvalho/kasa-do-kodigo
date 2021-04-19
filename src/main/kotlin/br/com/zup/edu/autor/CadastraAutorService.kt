package br.com.zup.edu.autor

import br.com.zup.edu.shared.handler.exceptions.AutorExistenteException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CadastraAutorService(@Inject private val repository: AutorRepository) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun cadastra(@Valid novoAutor: NovoAutor): Autor {
        logger.info("Cadastrando novo autor $novoAutor")

        if(repository.existsByEmail(novoAutor.email!!)){
            throw AutorExistenteException("Email existente")
        }

        val autor = novoAutor.toModel()
        repository.save(autor)

        logger.info("Autor com id: ${autor.id} criado com sucesso")

        return autor

    }

}