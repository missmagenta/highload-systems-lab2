package itmo.highload.api.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CreateFavoritesRequest(
    @field:NotBlank(message = "place id cannot be blank")
    val placeId: String,

    @field:NotBlank(message = "favorite type cannot be blank")
    @field:Pattern(regexp = "HOME|WORK|ENTERTAINMENT", message = "type must be either HOME, WORK, or ENTERTAINMENT")
    val favoriteType: String
)