package itmo.highload.model
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document("route")
data class Route(
    @Id
    val id: String? = null,

    @Size(min = 1, max = 100)
    @Field("name")
    var name: String,

    @Field("description")
    var description: String,

    @Size(min = 1)
    @Field("places")
    var places: List<PlaceId> = listOf()
)