package itmo.highload.model

import org.springframework.data.mongodb.core.mapping.Field

data class Grade(
    @Field("user_id")
    var userId: String,
    @Field("grade")
    var grade: Int
)
