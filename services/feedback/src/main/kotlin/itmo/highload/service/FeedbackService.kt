package itmo.highload.service

import itmo.highload.api.dto.CreatePlaceFeedbackRequest
import itmo.highload.api.dto.CreateRouteFeedbackRequest
import itmo.highload.api.dto.response.RouteResponse
import itmo.highload.model.Grade
import itmo.highload.model.PlaceFeedback
import itmo.highload.model.RouteFeedback
import itmo.highload.repository.PlaceFeedbackRepository
import itmo.highload.repository.RouteFeedbackRepository
import itmo.highload.service.contract.PlaceService
import itmo.highload.service.contract.RouteService
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FeedbackService(
    private val routeFeedbackRepository: RouteFeedbackRepository,
    private val placeFeedbackRepository: PlaceFeedbackRepository,
    private val routeService: RouteService,
    private val placeService: PlaceService
) {

    fun createRouteFeedback(feedback: CreateRouteFeedbackRequest, token: String): Mono<RouteFeedback> {
        return routeService.getRoute(feedback.routeId, token)
            .flatMap {
                routeFeedbackRepository.save(
                    RouteFeedback(
                        routeId = feedback.routeId,
                        grade = Grade(
                            userId = feedback.userId,
                            grade = feedback.grade,
                        )
                    )
                )
            }
            .switchIfEmpty(Mono.error(EntityNotFoundException("Route with ID ${feedback.routeId} not found")))
    }


    fun getRouteFeedbacks(routeId: String): Flux<RouteFeedback> {
        return routeFeedbackRepository.findByRouteId(routeId)
    }

    fun deleteRouteFeedback(feedbackId: String): Mono<Void> {
        return routeFeedbackRepository.findById(feedbackId)
            .switchIfEmpty(Mono.error(EntityNotFoundException("Feedback with ID $feedbackId not found")))
            .flatMap {
                routeFeedbackRepository.deleteById(feedbackId)
            }

    }

    fun createPlaceFeedback(feedback: CreatePlaceFeedbackRequest, token: String): Mono<PlaceFeedback> {
        return placeService.getPlace(feedback.placeId, token)
            .flatMap {
                placeFeedbackRepository.save(
                    PlaceFeedback(
                        placeId = feedback.placeId,
                        grade = Grade(
                            userId = feedback.userId,
                            grade = feedback.grade,
                        )
                    )
                )
            }
            .switchIfEmpty(Mono.error(EntityNotFoundException("Place with ID ${feedback.placeId} not found")))
    }

    fun getPlaceFeedbacks(routeId: String): Flux<PlaceFeedback> {
        return placeFeedbackRepository.findByPlaceId(routeId)
    }

    fun deletePlaceFeedback(feedbackId: String): Mono<Void> {
        return placeFeedbackRepository.findById(feedbackId)
            .switchIfEmpty(Mono.error(EntityNotFoundException("Feedback with ID $feedbackId not found")))
            .flatMap {
                placeFeedbackRepository.deleteById(feedbackId)
            }

    }
}