package itmo.highload.api.dto.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PlaceResponse(
    val id: String,
    val name: String,
    val coordinates: Coordinates,
    val tags: List<String>,
    val owners: List<String>,
    val description: String?,
)

data class Coordinates(
    val longitude: Double,
    val latitude: Double
)