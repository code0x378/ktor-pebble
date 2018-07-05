package com.thoughtlogix.ktor.pebble

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.respond
import io.ktor.routing.*

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Pebble) {
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
