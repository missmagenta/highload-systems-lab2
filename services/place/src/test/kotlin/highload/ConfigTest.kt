package highload

import io.mockk.every
import io.mockk.mockk
import itmo.highload.mongo.Config
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConfigTest {

    private val config: Config = mockk(relaxed = true)

    @Test
    fun `should inject mongo configuration values correctly`() {

        every { config.url } returns "mongodb://localhost:27017"
        every { config.database } returns "test_database"
        every { config.user } returns "test_user"
        every { config.password } returns "test_password"
        every { config.authSource } returns "admin"

        assertEquals("mongodb://localhost:27017", config.url)
        assertEquals("test_database", config.database)
        assertEquals("test_user", config.user)
        assertEquals("test_password", config.password)
        assertEquals("admin", config.authSource)
    }
}