package com.example.coplanning.globals

import android.app.Application
import java.util.*

class MappingElementsManager : Application() {

    fun getMappingElements(): ArrayList<String> {
        return MappingElements
    }

    fun addMappingElement(element: String) {
        MappingElements.add(element)
    }

    fun removeMappingElement(element: String) {
        MappingElements.remove(element)
    }

    fun clearMappingElements() {
        MappingElements.clear()
    }

    companion object {
        private var MappingElements =
            ArrayList<String>()
    }
}