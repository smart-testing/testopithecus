package ru.yandex.testopithecus.system

import androidx.test.uiautomator.By
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import ru.yandex.testopithecus.monkeys.state.StateModelMonkey
import ru.yandex.testopithecus.stateenricher.SimpleEnricher
import ru.yandex.testopithecus.ui.Monkey
import ru.yandex.testopithecus.ui.UiState
import java.io.File

class AndroidMonkeyScreenshots(private val device: UiDevice, private val applicationPackage: String,
                               private val apk: String, private val screenshotDir: File, url: String) :
        AndroidMonkey(device, applicationPackage, apk) {

    private val model: Monkey = StateModelMonkey(SimpleEnricher(url))

    companion object {
        const val ANDROID_LOCALHOST = "10.0.2.2"
        const val LONG_WAIT = 1000.toLong()
    }

    override fun performAction() {
        try {
            performActionImpl()
        } catch (e: StaleObjectException) {
            System.err.println(e.localizedMessage)
        }
    }

    private fun performActionImpl() {
        val elements = device.findObjects(By.pkg(applicationPackage))
        device.wait(Until.hasObject(By.pkg(applicationPackage).depth(0)), LONG_WAIT)
        val screenshot = AndroidElementParser.takeScreenshot(screenshotDir, device)
        val uiState = AndroidElementParser.parseWithScreenshot(elements, screenshot)
        val action = generateAction(uiState)
        val id = action.id?.toInt()
        val element = id?.let { elements[id] }
        AndroidActionPerformer(device, applicationPackage, apk, element).perform(action)
    }

    override fun generateAction(uiState: UiState) = model.generateAction(uiState)
}