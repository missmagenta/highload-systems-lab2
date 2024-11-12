package highload

import itmo.highload.api.dto.*
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlaceDtoValidationTest {

    private lateinit var validator: Validator

    @BeforeEach
    fun setup() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `should fail validation when name is blank`() {
        val animalDto = PlaceDto(
            name = "",
            coordinates = Coordinates(1.0, 1.0),
            tags = listOf("place"),
            description = "best",
        )

        val violations: Set<ConstraintViolation<PlaceDto>> = validator.validate(animalDto)
        assertEquals(2, violations.size)

        val notBlankViolation = violations.firstOrNull {
            it.constraintDescriptor.annotation.annotationClass.simpleName == "NotBlank"
        }
        assertEquals("name must not be blank", notBlankViolation?.message)
        assertTrue(notBlankViolation?.constraintDescriptor?.annotation is NotBlank)

        val sizeViolation =
            violations.firstOrNull { it.constraintDescriptor.annotation.annotationClass.simpleName == "Size" }
        assertEquals("name should be between 1 and 100 characters", sizeViolation?.message)
        assertTrue(sizeViolation?.constraintDescriptor?.annotation is Size)
    }

}
