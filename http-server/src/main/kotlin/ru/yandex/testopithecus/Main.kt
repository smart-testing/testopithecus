package ru.yandex.testopithecus

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import org.json.JSONObject
import ru.yandex.testopithecus.monkeys.log.LogMonkey
import ru.yandex.testopithecus.monkeys.log.ReplayMonkey
import ru.yandex.testopithecus.ui.Monkey
import ru.yandex.testopithecus.utilsCore.deserializeState
import ru.yandex.testopithecus.utilsCore.serializeAction

val model: Monkey = ReplayMonkey("minimaltodo")


fun Application.main() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    routing {
        post("/generate-action") {
            val jsonState = JSONObject(call.receiveText())
            val uiState = deserializeState(jsonState)
            val action = model.generateAction(uiState)
            val resp = serializeAction(action).toString()
            call.respond(resp)
        }
    }
    routing {
        post("/log") {
            val log = call.receiveText()
            if (model is LogMonkey) {
                model.appendToLog(log)
            }
        }
    }
}
