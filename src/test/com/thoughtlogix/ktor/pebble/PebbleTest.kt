import com.thoughtlogix.ktor.pebble.Pebble
import com.thoughtlogix.ktor.pebble.PebbleContent
import com.thoughtlogix.ktor.pebble.main
import io.ktor.application.*
import io.ktor.features.Compression
import io.ktor.features.ConditionalHeaders
import io.ktor.http.*
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.*
import org.junit.Test
import java.util.zip.GZIPInputStream
import kotlin.test.*

class PebbleTest {

    private val model = mapOf(
            "title" to "Ktor Pebble Test Page",
            "description" to "A Ktor feature to use the pebble template engine by Mitchell Bösecke",
            "myDogs" to listOf("Bebe", "Dot", "Brownie", "Bella")
    )

    @Test
    fun testName() {
        withTestApplication {
            application.install(Pebble)
            application.install(ConditionalHeaders)
            application.routing {
                get("/") {
                    call.respond(PebbleContent("example.peb", model, "e"))
                }
            }

            handleRequest(HttpMethod.Get, "/").response.let { response ->
                assertNotNull(response.content)
                assert(response.content!!.contains("<h1>Ktor Pebble Test Page</h1>"))
                assert(response.content!!.contains("<p>A Ktor feature to use the pebble template engine by Mitchell Bösecke</p>"))
                assert(response.content!!.contains("<li>Brownie</li>"))
                val contentTypeText = assertNotNull(response.headers[HttpHeaders.ContentType])
                assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), ContentType.parse(contentTypeText))
                assertEquals("e", response.headers[HttpHeaders.ETag])
            }
        }
    }

    @Test
    fun canRespondAppropriately() {
        withTestApplication {
            application.install(Pebble)
            application.install(ConditionalHeaders)
            application.routing {
                get("/") {
                    call.respond(PebbleContent("example.peb", model, "e"))
                }
            }

            val call = handleRequest(HttpMethod.Get, "/")

            with(call.response) {
                assertNotNull(content)
                assert(content!!.contains("<h1>Ktor Pebble Test Page</h1>"))
                assert(content!!.contains(other = "<p>A Ktor feature to use the pebble template engine by Mitchell Bösecke</p>"))
                assert(content!!.contains("<li>Brownie</li>"))
            }
        }
    }

    @Test
    fun testCompression() {
        withTestApplication {
            application.install(Pebble)
            application.install(Compression)
            application.install(ConditionalHeaders)

            application.routing {
                get("/") {
                    call.respond(PebbleContent("example.peb", model, "e"))
                }
            }

            handleRequest(HttpMethod.Get, "/") {
                addHeader(HttpHeaders.AcceptEncoding, "gzip")
            }.response.let { response ->
                val content = GZIPInputStream(response.byteContent!!.inputStream()).reader().readText()
                assertNotNull(content)
                assert(content.contains("<h1>Ktor Pebble Test Page</h1>"))
                assert(content.contains("<p>A Ktor feature to use the pebble template engine by Mitchell Bösecke</p>"))
                assert(content.contains("<li>Brownie</li>"))
                val contentTypeText = assertNotNull(response.headers[HttpHeaders.ContentType])
                assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), ContentType.parse(contentTypeText))
                assertEquals("e", response.headers[HttpHeaders.ETag])
            }
        }
    }

    @Test
    fun testWithoutEtag() {
        withTestApplication {
            application.install(Pebble)
            application.install(ConditionalHeaders)

            application.routing {
                get("/") {
                    call.respond(PebbleContent("example.peb", model))
                }
            }

            handleRequest(HttpMethod.Get, "/").response.let { response ->
                assertNotNull(response.content)
                assert(response.content!!.contains("<h1>Ktor Pebble Test Page</h1>"))
                assert(response.content!!.contains("<p>A Ktor feature to use the pebble template engine by Mitchell Bösecke</p>"))
                assert(response.content!!.contains("<li>Brownie</li>"))
                val contentTypeText = assertNotNull(response.headers[HttpHeaders.ContentType])
                assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), ContentType.parse(contentTypeText))
                assertEquals(null, response.headers[HttpHeaders.ETag])
            }
        }
    }

}