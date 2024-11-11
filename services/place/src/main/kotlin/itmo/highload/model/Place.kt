package itmo.highload.model

import jakarta.validation.constraints.Size
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


@Document("place")
data class Place(
    @Id
    val id: String? = null,

    @Size(min = 1, max = 100)
    @Field("name")
    var name: String,

    @GeoSpatialIndexed(name = "coordinates_2dsphere", type = GeoSpatialIndexType.GEO_2DSPHERE)
    @Field("coordinates")
    var coordinates: GeoJsonPoint,

    @Size(max = 10)
    @Field("tags")
    var tags: List<String> = listOf(),

    @Field("owners")
    var owners: List<OwnerId> = listOf(),

    @Size(max = 500)
    @Field("description")
    var description: String? = null
)
