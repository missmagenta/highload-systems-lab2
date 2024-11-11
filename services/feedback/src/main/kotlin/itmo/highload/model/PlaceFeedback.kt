package itmo.highload.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document("place_feedback")
data class PlaceFeedback(
    @Id
    val id: String? = null,

    @Field("place_id")
    var placeId: String,

    @Field("grade")
    var grade: Grade
)
