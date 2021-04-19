package br.com.zup.edu.categoria

import br.com.zup.edu.shared.handler.exceptions.CategoriaExistenteException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CadastraCategoriaService(@Inject private val repository: CategoriaRepository) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun cadastra(@Valid novaCategoria: NovaCategoria): Categoria {
        logger.info("Cadastrando nova categoria $novaCategoria")

        if(repository.existsByNome(novaCategoria.nome)) {
            throw CategoriaExistenteException("Categoria existente!")
        }

        val categoria = novaCategoria.toModel()
        repository.save(categoria)

        logger.info("Categoria com id: ${categoria.id} criada com sucesso")

        return categoria
    }

}
