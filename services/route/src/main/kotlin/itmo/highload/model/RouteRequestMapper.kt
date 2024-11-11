package itmo.highload.model

import itmo.highload.api.dto.CreateRouteRequest
import itmo.highload.api.dto.response.RouteResponse

object RouteRequestMapper {
    fun toEntity(request: CreateRouteRequest): Route {
        return Route(
            name = request.name,
            description = request.description,
            places = request.places,
        )
    }

    fun toResponse(entity: Route): RouteResponse {
        return RouteResponse(
            id = entity.id!!,
            name = entity.name,
            description = entity.description,
            places = entity.places,
        )
    }
}
