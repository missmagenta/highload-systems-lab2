package itmo.highload.model

import itmo.highload.security.Role
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate

@Document("user")
data class User(
    @Id
    val id: String? = null,
    @Field("login")
    val login: String,
    @Field("password")
    val password: String,
    @Field("role")
    val role: Role,
    @Field("registration_date")
    var registrationDate: LocalDate,
)