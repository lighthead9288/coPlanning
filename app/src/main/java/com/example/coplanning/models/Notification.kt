package com.example.coplanning.models

class Notification(private val DateTime: String, private val Sender: String, private val Description: String, private val Status: Boolean, val AdditionalInfo: NotificationChanges) {
    fun GetDateTime(): String {
        return DateTime
    }

    fun GetSender(): String {
        return Sender
    }

    fun GetDescription(): String {
        return Description
    }

    fun GetStatus(): Boolean {
        return Status
    }

    fun GetNameChanges(): String? {
        return AdditionalInfo.GetName()
    }

    fun GetCommentChanges(): String? {
        return AdditionalInfo.GetComment()
    }

    fun GetDateTimeFromChanges(): String? {
        return AdditionalInfo.GetDateTimeFrom()
    }

    fun GetDateTimeToChanges(): String? {
        return AdditionalInfo.GetDateTimeTo()
    }
}