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

    @Test
    fun `should fail validation when name exceeds maximum length`() {
        val placeDto = PlaceDto(
            name = "A".repeat(101),
            coordinates = Coordinates(1.0, 1.0),
            tags = listOf("place"),
            description = "A beautiful place"
        )

        val violations: Set<ConstraintViolation<PlaceDto>> = validator.validate(placeDto)
        assertEquals(1, violations.size)

        val sizeViolation = violations.first()
        assertEquals("name should be between 1 and 100 characters", sizeViolation.message)
    }

    @Test
    fun `should fail validation when tags exceed maximum size`() {
        val placeDto = PlaceDto(
            name = "Valid Name",
            coordinates = Coordinates(1.0, 1.0),
            tags = List(11) { "tag$it" },
            description = "A beautiful place"
        )

        val violations: Set<ConstraintViolation<PlaceDto>> = validator.validate(placeDto)
        assertEquals(1, violations.size)

        val sizeViolation = violations.first()
        assertEquals("no more than 10 tags", sizeViolation.message)
    }

    @Test
    fun `should fail validation when description exceeds maximum length`() {
        val placeDto = PlaceDto(
            name = "Valid Name",
            coordinates = Coordinates(1.0, 1.0),
            tags = listOf("place"),
            description = "A".repeat(501)
        )

        val violations: Set<ConstraintViolation<PlaceDto>> = validator.validate(placeDto)
        assertEquals(1, violations.size)

        val sizeViolation = violations.first()
        assertEquals("description should be no more than 500 characters", sizeViolation.message)
    }

    @Test
    fun `should pass validation for valid PlaceDto`() {
        val placeDto = PlaceDto(
            name = "Valid Name",
            coordinates = Coordinates(1.0, 1.0),
            tags = listOf("tag1", "tag2"),
            description = "A beautiful place"
        )

        val violations: Set<ConstraintViolation<PlaceDto>> = validator.validate(placeDto)
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `should fail validation when longitude is out of range`() {
        val coordinates = Coordinates(
            longitude = 181.0,
            latitude = 45.0
        )

        val violations: Set<ConstraintViolation<Coordinates>> = validator.validate(coordinates)
        assertEquals(1, violations.size)

        val maxViolation = violations.first()
        assertEquals("longitude should be no more than 180", maxViolation.message)
    }

    @Test
    fun `should fail validation when latitude is out of range`() {
        val coordinates = Coordinates(
            longitude = 90.0,
            latitude = -91.0
        )

        val violations: Set<ConstraintViolation<Coordinates>> = validator.validate(coordinates)
        assertEquals(1, violations.size)

        val minViolation = violations.first()
        assertEquals("latitude must be at least -90", minViolation.message)
    }

    @Test
    fun `should pass validation for valid coordinates`() {
        val coordinates = Coordinates(
            longitude = 45.0,
            latitude = 45.0
        )

        val violations: Set<ConstraintViolation<Coordinates>> = validator.validate(coordinates)
        assertTrue(violations.isEmpty())
    }

}
