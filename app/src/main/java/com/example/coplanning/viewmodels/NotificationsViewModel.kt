package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.BadgesOperations
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.models.notification.Notification
import com.example.coplanning.models.notification.NotificationChanges
import com.github.nkzawa.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class NotificationsViewModel(val application: Application) {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var sharedPrefs = SharedPreferencesOperations(application)

    private val socketClient: SocketClient = SocketClient()

    private val badgesOperations: BadgesOperations = BadgesOperations()

    private val _notifications = MutableLiveData<MutableList<Notification>>()
    val notifications: LiveData<MutableList<Notification>>
        get() = _notifications

    private val onGetNotifications = Emitter.Listener { args ->
        uiScope.launch {
            val sender = args[0] as String
            val description = args[1] as String
            val dateTime = args[2] as String
            val additionalInfo = args[3] as JSONObject

            val notificationChanges =
                getNotificationAdditionalInfoFromJSONObject(additionalInfo)

            val username = setCurUserName()

            val jsonNotification = JSONObject()
            try {
                jsonNotification.put("dateTime", dateTime)
                jsonNotification.put("receiver", username)
                jsonNotification.put("sender", sender)
                jsonNotification.put("description", description)
                jsonNotification.put("status", false)
                notifyAboutReadNotifications(JSONArray().put(jsonNotification))
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val newNotification =
                Notification(
                    dateTime,
                    sender,
                    description,
                    false,
                    notificationChanges!!
                )
            _notifications.value?.add(0, newNotification)

        }
    }

    private val onGetFullNotificationsList = Emitter.Listener { args ->
        uiScope.launch {
            val notifications = args[0] as JSONArray

            val unreadNotifications = JSONArray()

            val notificationsList = mutableListOf<Notification>()

            for (i in 0 until notifications.length()) {
                try {
                    val notification = notifications.getJSONObject(i)
                    val dateTime = notification.getString("dateTime")
                    val sender = notification.getString("sender")
                    val description = notification.getString("description")
                    val status = notification.getBoolean("status")
                    if (!status) unreadNotifications.put(notification)
                    val additionalInfo =
                        notification.getJSONObject("additionalInfo")
                    val notificationChanges =
                        getNotificationAdditionalInfoFromJSONObject(additionalInfo)
                    notificationsList.add(
                        Notification(
                            dateTime,
                            sender,
                            description,
                            status,
                            notificationChanges!!
                        )
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            _notifications.value = notificationsList

            notifyAboutReadNotifications(unreadNotifications)
        }
    }



    private fun getNotificationAdditionalInfoFromJSONObject(additionalInfo: JSONObject): NotificationChanges? {
        var nameChanges: String? = additionalInfo.getString("name")
        var commentChanges: String? = additionalInfo.getString("comment")
        var dateTimeFromChanges: String? = additionalInfo.getString("dateTimeFrom")
        var dateTimeToChanges: String? = additionalInfo.getString("dateTimeTo")

        val notificationChanges =
            NotificationChanges(
                nameChanges.toString(),
                commentChanges.toString(),
                dateTimeFromChanges.toString(),
                dateTimeToChanges.toString()
            )

        return notificationChanges
    }

    private fun notifyAboutReadNotifications(notifications: JSONArray) {
        val username = setCurUserName()
        socketClient.changeNotificationsStatus(username, notifications)
    }

    private fun setCurUserName(): String? { return sharedPrefs.login}

    init {
        val username = setCurUserName()

        badgesOperations.clearNotificationsAmount()

        socketClient.setNotificationsListener(onGetNotifications)

        socketClient.setFullNotificationsListListener(onGetFullNotificationsList)
        socketClient.getFullNotificationsList(username)
    }




}