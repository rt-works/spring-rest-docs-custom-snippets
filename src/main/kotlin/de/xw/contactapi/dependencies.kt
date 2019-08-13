package de.xw.contactapi

import de.xw.contactapi.handler.CreateContactHandler
import org.koin.dsl.module

val dependencies = module {
    single { CreateContactHandler() }
}
