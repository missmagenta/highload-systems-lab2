package itmo.highload.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document("favorites")
data class Favorites(
    @Id
    val id: String? = null,

    @Field("user_id")
    var userId: String,

    @Field("place_id")
    var placeId: String,

    var favorites: Favorite
)

enum class Favorite {
    HOME,
    WORK,
    ENTERTAINMENT,
}