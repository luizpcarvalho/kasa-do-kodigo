package br.com.zup.edu.shared.validation

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ExistsByIdValidator::class])
annotation class ExistsById(
    val message: String = "Item nao existente no banco de dados.",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
    val tabela: String
)

@Singleton
class ExistsByIdValidator(val entityManager: EntityManager): ConstraintValidator<ExistsById, Long> {

    @Transactional
    override fun isValid(
        value: Long?,
        annotationMetadata: AnnotationValue<ExistsById>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value == null) {
            return true
        }

        val tabela = annotationMetadata.stringValue("tabela").orElse("null")

        val query = entityManager.createQuery("SELECT 1 FROM $tabela WHERE id = :value")
        query.setParameter("value", value)
        val result = query.resultList

        return result.isNotEmpty()
    }
}