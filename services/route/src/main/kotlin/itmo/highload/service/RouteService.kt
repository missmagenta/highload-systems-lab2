package itmo.highload.service

import itmo.highload.api.dto.CreateRouteRequest
import itmo.highload.model.Route
import itmo.highload.repository.RouteRepository
import itmo.highload.exceptions.EntityNotFoundException
import itmo.highload.service.contract.PlaceService
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RouteService(
    private val routeRepository: RouteRepository,
    private val placeService: PlaceService,
) {
    fun getRoute(id: String): Mono<Route> = routeRepository.findById(id)
        .switchIfEmpty(Mono.error(EntityNotFoundException("Route with ID $id not found")))

    fun getAllRoutes(): Flux<Route> {
        return routeRepository.findAll()
    }

    fun createRoute(request: CreateRouteRequest, token: String): Mono<Route> {
        val placesCheck = Flux.fromIterable(request.places)
            .flatMap { placeId ->
                placeService.getPlace(placeId, token)
                    .switchIfEmpty(Mono.error(EntityNotFoundException("Place with id $placeId not found")))
            }
            .collectList()

        return placesCheck.flatMap {
            val route = Route(
                name = request.name,
                description = request.description,
                places = request.places
            )
            routeRepository.save(route)
        }
    }


    fun deleteRoute(id: String, userId: String): Mono<Void> = routeRepository.deleteById(id)

    fun findRoutesByPlaceContains(placeId: String): Flux<Route> {
        return routeRepository.findAllByPlacesContains(placeId)
    }

}
