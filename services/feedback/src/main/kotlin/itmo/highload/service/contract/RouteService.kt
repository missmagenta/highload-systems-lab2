package itmo.highload.service.contract

import itmo.highload.api.dto.response.RouteResponse
import itmo.highload.model.RouteFeedback
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient(
    value = "route-service",
    url = "http://localhost:8085/api/v1",
    fallback = RouteServiceFallback::class
)
interface RouteService {
    @GetMapping("/route/{id}")
    fun getRoute(@PathVariable id: String, @RequestHeader("Authorization") token: String): Mono<RouteResponse>
}

@Component
class RouteServiceFallback : RouteService {
    override fun getRoute(
        @PathVariable id: String,
        @RequestHeader("Authorization") token: String
    ): Mono<RouteResponse> {
        return Mono.empty()
    }
}

