package itmo.highload.api.dto.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class RouteResponse(
    val id: String,
    val name: String,
    val description: String,
    val places: List<String>
)
