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
@Constraint(validatedBy = [UniqueValueValidator::class])
annotation class UniqueValue(
    val message: String = "Item já existente no banco de dados.",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
    val campo: String,
    val tabela: String
)

@Singleton
class UniqueValueValidator(val entityManager: EntityManager): ConstraintValidator<UniqueValue, String> {

    @Transactional
    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<UniqueValue>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value.isNullOrBlank()) {
            return true
        }

        val campo = annotationMetadata.stringValue("campo").orElse("null")
        val tabela = annotationMetadata.stringValue("tabela").orElse("null")
        println("Campo: $campo - Tabela: $tabela")

        val query = entityManager.createQuery("SELECT 1 FROM $tabela WHERE $campo = :value")
        query.setParameter("value", value)
        val result = query.resultList

        return result.isEmpty()
    }
}


