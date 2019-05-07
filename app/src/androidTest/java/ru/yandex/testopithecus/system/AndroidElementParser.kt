package ru.yandex.testopithecus.system

import androidx.test.uiautomator.*
import ru.yandex.testopithecus.ui.UiElement
import ru.yandex.testopithecus.ui.UiState
import java.util.stream.Collectors

object AndroidElementParser {
    fun parse(elements: List<UiObject2>): UiState {
        return UiState(parseElements(elements), buildGlobal())
    }

    private fun parseElements(elements: List<UiObject2>): List<UiElement> {
        var id = 0
        return elements.stream()
                .map { element -> parse(id.toString(), element) }
                .peek { id++ }
                .collect(Collectors.toList())
    }

    private fun parse(elementId: String, element: UiObject2): UiElement {
        return UiElement(elementId, parseAttributes(element), parsePossibleActions(element))
    }

    private fun parseAttributes(element: UiObject2): MutableMap<String, Any> {
        val center = element.visibleCenter
        return mutableMapOf("location" to Pair(Pair("x", center.x), Pair("y", center.y)),
                "text" to element.text,
                "isLabel" to isLabelElement(element),
                "rect" to RectAndroid(element.visibleBounds),
                "id" to element.resourceName)
    }

    private fun parsePossibleActions(element: UiObject2): List<String> {
        val possibleActions = mutableListOf<String>()
        if (isInputElement(element)) {
            possibleActions.add("INPUT")
        }
        if (isTapElement(element)) {
            possibleActions.add("TAP")
        }
        if (element.className.contains("EditText")) {
            possibleActions.add("FILL")
        }
        return possibleActions
    }

    private fun buildGlobal(): Map<String, Any> {
        return mapOf()
    }

    private fun isLabelElement(element: UiObject2): Boolean {
        return (element.className == "android.widget.TextView" || element.className == "TextInputLayout")
                && element.text != null && element.text.isNotEmpty() && !element.isClickable
    }

    private fun isTapElement(element: UiObject2): Boolean {
        return (element.className == "android.widget.TextView"
                || element.className == "android.widget.ImageButton"
                || element.className == "android.widget.ImageView"
                || element.className == "android.widget.TextView"
                || element.className == "android.widget.Button"
                || element.className == "android.widget.LinearLayout")
                && element.isClickable
    }

    private fun isInputElement(element: UiObject2): Boolean {
        return element.className == "android.widget.EditText"
    }
}