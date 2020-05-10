package com.example.coplanning.models

import java.util.*

class SavedMapping(
    private val DateTimeFrom: Calendar,
    private val DateTimeTo: Calendar,
    private val Participants: ArrayList<String>
) {
    fun GetDateTimeFrom(): Calendar {
        return DateTimeFrom
    }

    fun GetDateTimeTo(): Calendar {
        return DateTimeTo
    }

    fun GetParticipants(): ArrayList<String> {
        return Participants
    }

}