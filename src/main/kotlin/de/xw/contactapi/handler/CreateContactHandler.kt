package de.xw.contactapi.handler

import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.response.respond
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import mu.KLogging
import java.util.*
import kotlin.system.measureTimeMillis

class CreateContactHandler {
    companion object : KLogging()

    suspend fun handle(call: ApplicationCall) {

        val time = measureTimeMillis {
            coroutineScope {
                val requestDeffered = GlobalScope.async {
                    logger.debug { "Getting request" }
                    delay(2000L)
                    call.receive(CreateContactRequest::class)
                }


                val smthDeferred = GlobalScope.async {
                    doSomethingUseful()
                }

                val request = requestDeffered.await()
                val smth = smthDeferred.await()

                logger.debug(fun(): Any? {
                    return "handling create contact request: ${request} + $smth"
                })
            }
        }


        logger.info { "Completed in $time ms" }

        //do handling
        val response = CreateContactResponse(UUID.randomUUID().toString())
        call.respond(response)
    }

    private suspend fun doSomethingUseful() {
        delay(2000L)
        logger.debug { "doin' smt useful" }
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
