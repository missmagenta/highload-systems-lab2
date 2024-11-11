package itmo.highload.model

import itmo.highload.api.dto.CreateRouteFeedbackRequest
import itmo.highload.api.dto.response.PlaceFeedbackResponse
import itmo.highload.api.dto.response.RouteFeedbackResponse

object FeedbackMapper {
    fun toEntityRoute(request: CreateRouteFeedbackRequest): RouteFeedback {
        return RouteFeedback(
            routeId = request.routeId,
            grade = Grade(request.userId, request.grade)
        )
    }

    fun toRouteResponse(entity: RouteFeedback): RouteFeedbackResponse {
        return RouteFeedbackResponse(
           id = entity.id!!,
            routeId = entity.routeId,
            userId = entity.grade.userId,
            grade = entity.grade.grade
        )
    }

    fun toEntityPlace(request: CreateRouteFeedbackRequest): RouteFeedback {
        return RouteFeedback(
            routeId = request.routeId,
            grade = Grade(request.userId, request.grade)
        )
    }

    fun toPlaceResponse(entity: PlaceFeedback): PlaceFeedbackResponse {
        return PlaceFeedbackResponse(
            id = entity.id!!,
            placeId = entity.placeId,
            userId = entity.grade.userId,
            grade = entity.grade.grade
        )
    }



}
