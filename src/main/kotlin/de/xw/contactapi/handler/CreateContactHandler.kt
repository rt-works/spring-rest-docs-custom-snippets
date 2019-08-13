package de.xw.contactapi.handler

import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.response.respond
import mu.KLogging
import java.util.*

class CreateContactHandler {
    companion object : KLogging()

    suspend fun handle(call: ApplicationCall) {
        val request = call.receive(CreateContactRequest::class)
        logger.debug { "handling create contact request: $request" }
        //do handling
        val response = CreateContactResponse(UUID.randomUUID().toString())
        call.respond(response)
    }
}

data class CreateContactRequest(
    val contactKey: String,
    val type: ContactType
)

data class CreateContactResponse(
    val contactKey: String
)

enum class ContactType {
    ADMIN,
    END_USER,
    TESTER
}
