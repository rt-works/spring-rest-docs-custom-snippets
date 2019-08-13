package de.xw.contactapi.docs.snippet

import de.xw.contactapi.handler.ContactType
import org.springframework.restdocs.operation.Operation
import org.springframework.restdocs.snippet.TemplatedSnippet

class ContactTypesSnippet(snippetName: String?, attributes: MutableMap<String, Any>?)
    : TemplatedSnippet(snippetName, attributes) {

    constructor() : this("contact-types", null)

    override fun createModel(operation: Operation?): Map<String, Any> {
        return mapOf(
            "contactTypes" to ContactType.values()
        )
    }
}

fun contactTypesSnippet() = ContactTypesSnippet()
