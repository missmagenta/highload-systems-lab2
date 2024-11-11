package itmo.highload.api.dto.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PlaceFeedbackResponse(
    val id: String,
    val placeId: String,
    val userId: String,
    val grade: Int,
)