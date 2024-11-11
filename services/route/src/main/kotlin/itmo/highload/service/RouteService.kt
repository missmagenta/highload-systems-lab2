package itmo.highload.service

import itmo.highload.api.dto.CreateRouteRequest
import itmo.highload.model.Route
import itmo.highload.model.RouteRequestMapper
import itmo.highload.repository.RouteRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RouteService(
    private val routeRepository: RouteRepository
) {
    fun getRoute(id: String): Mono<Route> = routeRepository.findById(id)
        .switchIfEmpty(Mono.error(EntityNotFoundException("Route with ID $id not found")))

    fun getAllRoutes(): Flux<Route> {
        return routeRepository.findAll()
    }

    fun createRoute(request: CreateRouteRequest, userId: String): Mono<Route> = 
        routeRepository.save(RouteRequestMapper.toEntity(request))

    fun deleteRoute(id: String, userId: String): Mono<Void> = routeRepository.deleteById(id)

    fun findRoutesByPlaceContains(placeId: String): Flux<Route> {
        return routeRepository.findAllByPlacesContains(placeId)
    }

}
