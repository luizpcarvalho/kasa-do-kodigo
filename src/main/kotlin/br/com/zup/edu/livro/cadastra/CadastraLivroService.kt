package br.com.zup.edu.livro.cadastra

import br.com.zup.edu.autor.AutorRepository
import br.com.zup.edu.categoria.CategoriaRepository
import br.com.zup.edu.livro.Livro
import br.com.zup.edu.livro.LivroRepository
import br.com.zup.edu.shared.handler.exceptions.LivroExistenteException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CadastraLivroService(
    @Inject private val repository: LivroRepository,
    @Inject private val autorRepository: AutorRepository,
    @Inject private val categoriaRepository: CategoriaRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun cadastra(@Valid novoLivro: NovoLivro): Livro {

        logger.info("Cadastrando novo livro $novoLivro")

        if(repository.existsByIsbn(novoLivro.isbn!!) || repository.existsByTitulo(novoLivro.titulo!!)) {
            throw LivroExistenteException("Livro existente")
        }

        val autor = autorRepository.findById(novoLivro.autorId!!.toLong()).get()
        val categoria = categoriaRepository.findById(novoLivro.categoriaId!!.toLong()).get()

        val livro = novoLivro.toModel(categoria, autor)
        repository.save(livro)
        autor.associaLivro(livro)
        categoria.associaLivro(livro)

        logger.info("Livro com id: ${livro.id} criado com sucesso")

        return livro
    }

}