package com.tlogx.ktor.pebble

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.template.PebbleTemplate
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.http.ContentType
import io.ktor.http.charset
import io.ktor.http.content.EntityTagVersion
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.versions
import io.ktor.http.withCharset
import io.ktor.response.ApplicationSendPipeline
import io.ktor.util.AttributeKey
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.cio.bufferedWriter
import io.ktor.utils.io.ByteWriteChannel
import java.util.*


/**
 * Represents a response content that could be used to respond with `call.respond(PebbleContent(...))`
 *
 * @param template name to be resolved by pebble
 * @param model to be passed to the template
 * @param etag header value (optional)
 * @param contentType (optional, `text/html` with UTF-8 character encoding by default)
 */
class PebbleContent(val template: String,
                    val model: Map<String, Any>,
                    val etag: String? = null,
                    val contentType: ContentType = ContentType.Text.Html.withCharset(Charsets.UTF_8))


/**
 * Configurable ktor/Pebble options
 *
 * See https://pebbletemplates.io/wiki/guide/installation/
 */
class Configuration {
    var templateDir = ""
    var strictVariables = false
    var defaultLocale = Locale.getDefault()
    var cacheActive = true
    var allowGetClass = false
    var greedyMatchMethod = false

}

/**
 * Pebble ktor feature
 */
class Pebble(configuration: Configuration) {
    private var templateDir = configuration.templateDir
    private var engine = PebbleEngine.Builder()
            .strictVariables(configuration.strictVariables)
            .defaultLocale(configuration.defaultLocale)
            .build()

    /**
     * A companion object for installing feature
     */
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, Pebble> {
        override val key = AttributeKey<Pebble>("pebble")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Pebble {
            val feature = Pebble(Configuration().apply(configure))
            pipeline.sendPipeline.intercept(ApplicationSendPipeline.Transform) { value ->
                if (value is PebbleContent) {
                    val response = feature.process(value)
                    proceedWith(response)
                }
            }
            return feature
        }
    }

    private fun process(content: PebbleContent): PebbleOutgoingContent {
        return PebbleOutgoingContent(engine.getTemplate(templateDir + content.template), content.model, content.etag, content.contentType)
    }

    private class PebbleOutgoingContent(val template: PebbleTemplate,
                                        val model: Map<String, Any>,
                                        etag: String?,
                                        override val contentType: ContentType) : OutgoingContent.WriteChannelContent() {
        @KtorExperimentalAPI
        override suspend fun writeTo(channel: ByteWriteChannel) {
            channel.bufferedWriter(contentType.charset() ?: Charsets.UTF_8).use {
                template.evaluate(it, model)
            }
        }

        init {
            if (etag != null)
                versions += EntityTagVersion(etag)
        }
    }
}