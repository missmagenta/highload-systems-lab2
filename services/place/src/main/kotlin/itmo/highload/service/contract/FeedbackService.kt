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
    value = "feedback-service",
    url = "http://localhost:8088/api/v1",
    fallback = FeedbackServiceFallback::class
)
interface FeedbackService {
    @DeleteMapping("/feedback/place/{id}")
    fun deleteFeedbacksForPlace(@PathVariable("id") id: String): Mono<Void>
}

@Component
class FeedbackServiceFallback : FeedbackService {
    override fun deleteFeedbacksForPlace(id: String): Mono<Void> {
        return Mono.empty()
    }
}