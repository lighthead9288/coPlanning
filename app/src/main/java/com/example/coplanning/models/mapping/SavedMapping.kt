package com.example.coplanning.models.mapping

import java.util.*

data class SavedMapping(
    val dateTimeFrom: Calendar,
    val dateTimeTo: Calendar,
    val participants: ArrayList<String>
)