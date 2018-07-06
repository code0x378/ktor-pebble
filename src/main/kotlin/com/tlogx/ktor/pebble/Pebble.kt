package com.tlogx.ktor.pebble

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.template.PebbleTemplate
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.cio.bufferedWriter
import io.ktor.content.EntityTagVersion
import io.ktor.content.OutgoingContent
import io.ktor.content.versions
import io.ktor.http.ContentType
import io.ktor.http.charset
import io.ktor.http.withCharset
import io.ktor.response.ApplicationSendPipeline
import io.ktor.util.AttributeKey
import kotlinx.coroutines.experimental.io.ByteWriteChannel
import java.util.*


class PebbleContent(val template: String,
                    val model: Map<String, Any>,
                    val etag: String? = null,
                    val contentType: ContentType = ContentType.Text.Html.withCharset(Charsets.UTF_8))

class Configuration {
    var templateDir = ""
    var strictVariables = false
    var defaultLocale = Locale.getDefault()
}

class Pebble(configuration: Configuration) {
    var templateDir = configuration.templateDir
    var engine = PebbleEngine.Builder()
            .strictVariables(configuration.strictVariables)
            .defaultLocale(configuration.defaultLocale)
            .build();

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