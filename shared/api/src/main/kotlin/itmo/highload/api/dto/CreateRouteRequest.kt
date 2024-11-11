package itmo.highload.api.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CreateRouteRequest(
    @field:NotBlank(message = "name must not be blank")
    @field:Size(min = 1, max = 100, message = "name should be between 1 and 100 characters")
    val name: String,

    val description: String,

    @field:Size(min = 1, message = "at least one waypoint must be specified")
    val places: List<String>
)