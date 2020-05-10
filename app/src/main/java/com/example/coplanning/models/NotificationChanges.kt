package com.example.coplanning.models

class NotificationChanges {
    private var Name: String? = null
    private var Comment: String? = null
    private var DateTimeFrom: String? = null
    private var DateTimeTo: String? = null
    fun GetName(): String? {
        return Name
    }

    fun GetComment(): String? {
        return Comment
    }

    fun GetDateTimeFrom(): String? {
        return DateTimeFrom
    }

    fun GetDateTimeTo(): String? {
        return DateTimeTo
    }

    fun SetName(name: String?) {
        Name = name
    }

    fun SetComment(comment: String?) {
        Comment = comment
    }

    fun SetDateTimeFrom(dateTimeFrom: String?) {
        DateTimeFrom = dateTimeFrom
    }

    fun SetDateTimeTo(dateTimeTo: String?) {
        DateTimeTo = dateTimeTo
    }
}