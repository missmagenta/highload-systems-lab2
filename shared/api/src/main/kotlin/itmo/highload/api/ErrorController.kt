package itmo.highload.api

import itmo.highload.exceptions.EntityAlreadyExistsException
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ErrorController {
    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    fun onAuthenticationException(e: AuthenticationException): String? {
        return e.message
    }

    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun onEntityNotFoundException(e: EntityNotFoundException): String? {
        return e.message
    }

    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun onEntityAlreadyExistsException(e: EntityAlreadyExistsException): String? {
        return e.message
    }

}
