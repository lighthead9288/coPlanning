package com.example.coplanning.globals

import android.app.Application
import java.util.*

class MappingElementsManager : Application() {
    fun GetMappingElements(): ArrayList<String> {
        return MappingElements
    }

    fun SetMappingsElements(elements: ArrayList<String>) {
        MappingElements = elements
    }

    fun AddMappingElement(element: String) {
        MappingElements.add(element)
    }

    fun RemoveMappingElement(element: String) {
        MappingElements.remove(element)
    }

    fun ClearMappingElements() {
        MappingElements.clear()
    }

    companion object {
        private var MappingElements =
            ArrayList<String>()
    }
}