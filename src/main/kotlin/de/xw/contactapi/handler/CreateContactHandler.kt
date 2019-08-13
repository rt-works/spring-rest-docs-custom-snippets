package de.xw.contactapi.handler

import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KLogging
import java.util.*

class CreateContactHandler {
    companion object : KLogging()

    suspend fun handle(call: ApplicationCall): CreateContactResponse {
        logger.debug { "handling create contact request" }
        logger.debug("before getting request")

        GlobalScope.launch {
            logger.debug("Got request")
            val request = call.receive(CreateContactRequest::class)
            delay(2000)
            logger.debug { request }
        }

        logger.debug { "after launch" }

        return CreateContactResponse(UUID.randomUUID().toString())
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
