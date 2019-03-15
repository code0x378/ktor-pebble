package com.tlogx.ktor.pebble

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import java.util.*

/**
 * Sample application
 */
fun Application.main() {
    install(DefaultHeaders)
    install(Pebble) {
        templateDir = ""  // defaults to root of resources folder
        strictVariables = true
        defaultLocale = Locale.US
    }
    routing {
        get("/") {
            val model = mapOf(
                    "title" to "Ktor Pebble Test Page",
                    "description" to "A Ktor feature to use the pebble template engine by Mitchell Bösecke",
                    "myDogs" to listOf("Bebe", "Dot", "Brownie", "Bella")
            )
            call.respond(PebbleContent("example.peb", model, "e"))
        }
    }
}