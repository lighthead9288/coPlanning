package com.example.coplanning.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.coplanning.R

class MappingElementsAdapter(
    context: Context?,
    private val layout: Int,
    private val mapElements: List<String>,
    private val removeFromMappingCallback: (element: String) -> Unit
) : ArrayAdapter<String?>(context!!, layout, mapElements) {

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        val mapElement = mapElements[position]
        val usernameTv = view.findViewById<TextView>(R.id.username)
        usernameTv.text = mapElement
        val closeButton = view.findViewById<ImageButton>(R.id.close)
        closeButton.setOnClickListener { v -> removeFromMappingCallback(mapElement) }
        return view
    }
}