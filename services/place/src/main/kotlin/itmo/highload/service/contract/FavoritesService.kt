package itmo.highload.service.contract

import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@ReactiveFeignClient(
    value = "favorites-service",
    url = "http://localhost:8089/api/v1",
    fallback = FavoritesServiceFallback::class
)
interface FavoritesService {
    @DeleteMapping("/favorites/batch/{id}")
    fun deleteFavoritesForPlace(@PathVariable("id") id: String): Mono<Void>
}

@Component
class FavoritesServiceFallback : FavoritesService {
    override fun deleteFavoritesForPlace(id: String): Mono<Void> {
        return Mono.empty()
    }
}
