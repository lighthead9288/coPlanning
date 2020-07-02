package com.example.coplanning.models.notification

data class Notification(
    val dateTime: String,
    val sender: String,
    val description: String,
    val status: Boolean,
    val additionalInfo: NotificationChanges
)