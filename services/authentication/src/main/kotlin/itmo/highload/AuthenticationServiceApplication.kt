package itmo.highload

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.context.annotation.Import
import itmo.highload.Config

@EnableWebFlux
@Import(Config::class)
@SpringBootApplication(
    exclude = [
        SecurityAutoConfiguration::class,
        UserDetailsServiceAutoConfiguration::class
    ]
)
class AuthenticationServiceApplication
@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<AuthenticationServiceApplication>(*args)
}
