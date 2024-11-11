package itmo.highload.api.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.Valid
import jakarta.validation.constraints.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PlaceDto(

    @field:NotBlank(message = "name must not be blank")
    @field:Size(min = 1, max = 100, message = "name should be between 1 and 100 characters")
    val name: String,

    @field:Valid
    val coordinates: Coordinates,

    @field:Size(max = 10, message = "no more than 10 tags")
    val tags: List<String>,

    @field:Size(max = 500, message = "description should be no more than 500 characters")
    val description: String
)

data class Coordinates(
    @field:NotNull(message = "longitude must not be null")
    @field:DecimalMin(value = "-180.0", message = "longitude must be at least -180")
    @field:DecimalMax(value = "180.0", message = "longitude should be no more than 180")
    val longitude: Double,

    @field:NotNull(message = "latitude must not be null")
    @field:DecimalMin(value = "-90.0", message = "latitude must be at least -90")
    @field:DecimalMax(value = "90.0", message = "latitude should be no more than 90")
    val latitude: Double
)