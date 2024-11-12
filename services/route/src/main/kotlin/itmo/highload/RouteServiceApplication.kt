package itmo.highload

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.hystrix.EnableHystrix
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.web.reactive.config.EnableWebFlux
import reactivefeign.spring.config.EnableReactiveFeignClients
import org.springframework.context.annotation.Import

@EnableWebFlux
@EnableFeignClients
@EnableReactiveFeignClients
@EnableHystrix
@Import(Config::class)
@SpringBootApplication(
    exclude = [
        SecurityAutoConfiguration::class,
        UserDetailsServiceAutoConfiguration::class
    ]
)
class RouteServiceApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<RouteServiceApplication>(*args)
}
