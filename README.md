# Spring REST Docs. Code based documentation
This blog post is not going to cover the basics of using the Spring REST Docs. 
On the internet you can find several tutorials on how to get started with it. I.e. this or this one. 
The goal of this blog post is to provide more insights into an  advanced feature, namely - generating custom documentation snippets, which are based on your code. 
For instance such snippet can contain a table with all possible enumeration values of a field in request payload for a RESTful API.
## Technical stack
For a sample project I chose following technologies:
- Kotlin programming language
- Ktor framework for implementing of the sample RESTful API
- Koin framework for the dependency injection
- JUnit 5 for running tests
- REST Assured in conjunction with Spring REST Docs for generating of documentation
- Gradle (Kotlin) for building 
The source code of the project can be found here. 
 
## Project overview
As a sample project I prepared a simple Contact API for creating of contacts. I prepared a Ktor-`Application`:
```kotlin
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
```
You see here a defined `POST`-route `/contacts/` for creating of a new contact. Call to the route is passed to the  
`CreateContactHandler` (injected by `Koin`):
```kotlin
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
```
The handler takes incoming request, deserialize JSON payload, handle it, respond with another JSON payload.

## Documentation generation
To generate documentation I use following unit test:
```kotlin
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
```
The code represents typical `Spring REST Docs` test, which starts embedded web server and send a request to it.
Thanks to `RestDocumentationExtension` and corresponding configuration in the `setup()` standard snippets are
generated into the `build/generated-snippets` folder:
```bash
✔ 18:37 ~/projects/contactapi/build/generated-snippets/create-contact $ ll
total 56
Aug 12 14:17 .
Aug  9 17:28 ..
Aug 12 14:21 contact-types.adoc
Aug 12 14:21 curl-request.adoc
Aug 12 14:21 http-request.adoc
Aug 12 14:21 http-response.adoc
Aug 12 14:21 httpie-request.adoc
Aug 12 14:21 request-body.adoc
Aug 12 14:21 response-body.adoc
```
