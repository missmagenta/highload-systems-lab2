package itmo.highload.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@PropertySource("classpath:mongo.properties")
@EnableReactiveMongoRepositories(basePackages = ["itmo.highload.repository"])
class Config: AbstractReactiveMongoConfiguration() {

    @Value("\${mongo.url}")
    val url: String? = null

    @Value("\${mongo.db}")
    val database: String? = null

    @Value("\${mongo.user}")
    val user: String? = null

    @Value("\${mongo.password}")
    val password: String? = null

    @Value("\${mongo.authSource}")
    val authSource: String? = null

    public override fun getDatabaseName(): String = database!!

    public override fun mongoClientSettings(): MongoClientSettings {
        val builder = MongoClientSettings.builder()
        builder.applyConnectionString(ConnectionString(url!!))
            .credential(
                MongoCredential.createScramSha1Credential(
                    user!!,
                    authSource!!,
                    password!!.toCharArray()))
        this.configureClientSettings(builder)
        return builder.build()
    }
}
