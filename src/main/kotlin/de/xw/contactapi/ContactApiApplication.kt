package de.xw.contactapi

import de.xw.contactapi.handler.CreateContactHandler
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.slf4j.event.Level

fun main(args: Array<String>) {
    // Start Ktor
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

fun Application.main() {
    install(Koin) {
        modules(dependencies)
    }
    install(DefaultHeaders) {
    }
    install(CallLogging) {
        level = Level.DEBUG
    }

    install(ContentNegotiation) {
        jackson {
            // Configure Jackson's ObjectMapper here
        }
    }

    val createContactHandler: CreateContactHandler by inject()

    routing {
        post("/contacts/") {
            createContactHandler.handle(call)
        }
    }
}
