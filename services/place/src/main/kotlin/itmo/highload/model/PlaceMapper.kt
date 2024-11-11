package itmo.highload.model

import itmo.highload.api.dto.PlaceDto
import itmo.highload.api.dto.response.Coordinates
import itmo.highload.api.dto.response.PlaceResponse
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

object PlaceMapper {
    fun toEntity(ownerId: String, place: PlaceDto): Place {
        return Place(
            name = place.name,
            coordinates = GeoJsonPoint(place.coordinates.longitude, place.coordinates.latitude),
            owners = listOf(ownerId),
            tags = place.tags,
            description = place.description,
        )
    }

    fun toPlaceResponse(place: Place): PlaceResponse {
        return PlaceResponse(
            id = place.id!!,
                name = place.name,
                coordinates = Coordinates(longitude = place.coordinates.x, latitude = place.coordinates.y),
                owners = place.owners,
                tags = place.tags,
                description = place.description,
        )
    }
}
