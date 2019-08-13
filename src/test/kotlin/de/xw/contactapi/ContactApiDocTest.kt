package de.xw.contactapi

import de.xw.contactapi.docs.snippet.contactTypesSnippet
import de.xw.contactapi.handler.ContactType
import de.xw.contactapi.handler.CreateContactRequest
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import java.util.*

@ExtendWith(RestDocumentationExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactApiDocTest {
    private val embeddedServer = embeddedServer(factory = Netty, port = 8080, module = Application::main)
        .apply { start() }

    lateinit var spec: RequestSpecification


    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        this.spec = RequestSpecBuilder()
            .addFilter(
                RestAssuredRestDocumentation.documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(
                        Preprocessors.prettyPrint(),
                        Preprocessors.removeHeaders("Host", "Content-Length")
                    )
                    .withResponseDefaults(
                        Preprocessors.prettyPrint(),
                        Preprocessors.removeHeaders("Date", "Content-Length")
                    )
                    .and()
                    .snippets().withAdditionalDefaults(contactTypesSnippet())
            )
            .build()
    }

    @Test
    fun `should generate docs`() {
        given(this.spec)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .filter(document("create-contact"))
            .body(CreateContactRequest(contactKey = UUID.randomUUID().toString(), type = ContactType.END_USER))
            .`when`()
            .port(8080)
            .post("/contacts")
            .then()
            .assertThat()
            .statusCode(200)
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
    }
}
