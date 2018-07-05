package com.thoughtlogix.ktor.pebble

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


class PebbleContent(val template: String,
                    val model: Map<String, Any>,
                    val etag: String? = null,
                    val contentType: ContentType = ContentType.Text.Html.withCharset(Charsets.UTF_8))

class Pebble() {

    private var engine: PebbleEngine = PebbleEngine.Builder().build();
    //    private var engine: PebbleEngine  = PebbleEngine.Builder().extension(CoreExtension()).build();

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, PebbleEngine, Pebble> {
        override val key = AttributeKey<Pebble>("pebble")

        override fun install(pipeline: ApplicationCallPipeline, configure: PebbleEngine.() -> Unit): Pebble {
            //            val config = VeloEngine().apply(configure)
            val feature = Pebble()
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
        return PebbleOutgoingContent(engine.getTemplate(content.template), content.model, content.etag, content.contentType)
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
