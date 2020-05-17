package com.example.coplanning.communication

import android.app.Activity
import android.app.Application
import android.util.Log
import com.example.coplanning.globals.BadgesOperations
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.engineio.client.Socket
import com.github.nkzawa.socketio.client.IO
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.net.URISyntaxException


class SocketClient(): Application() {

    constructor(act: Activity?, ops: BadgesOperations?): this() {
        activity = act
        badgesOperations = ops
    }

    private var activity: Activity? = null

    private var badgesOperations: BadgesOperations? = null

    private var mSocket: com.github.nkzawa.socketio.client.Socket? =
       // IO.socket("http://10.0.2.2:3000/")
        IO.socket("https://co-planning.herokuapp.com/")



    private var SearchAnswerListener: Emitter.Listener? = null
    private var SavedMappingsListener: Emitter.Listener? = null
    private var UnavailableTimeAnswerListener: Emitter.Listener? = null
    private var MappingAnswerListener: Emitter.Listener? = null
    private var UserSubscribeListener: Emitter.Listener? = null
    private var UserTaskSubscribeListener: Emitter.Listener? = null

    private var NotificationsListener: Emitter.Listener? = null
    private var FullNotificationsListListener: Emitter.Listener? = null
    private var CurrentUser: String? = null

    private val DefaultNotificationsListener: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any?) {
            activity!!.runOnUiThread {
                val sender = args[0] as String?
                val description = args[1] as String?
                val notifAmount: Int = badgesOperations?.GetNotificationsAmount()!! + 1
                badgesOperations?.SetNotificationsAmount(notifAmount)
            }
        }
    }

    private val DefaultFullNotificationsListListener: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            activity!!.runOnUiThread {
                val notifications = args[0] as JSONArray
                var unreadNotificationsAmount = 0
                for (i in 0 until notifications.length()) {
                    try {
                        val notification = notifications.getJSONObject(i)
                        val status = notification.getBoolean("status")
                        if (!status) unreadNotificationsAmount++
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                badgesOperations?.SetNotificationsAmount(unreadNotificationsAmount)
            }
        }
    }

    fun Connect() {
        mSocket?.connect()

    }

    fun Disconnect() {
        mSocket?.disconnect()
    }

    fun OffListeners() {
        mSocket?.off()
    }

    fun SetSearchAnswerListener(listener: Emitter.Listener?) {
        SearchAnswerListener = listener
        mSocket?.on("search", SearchAnswerListener)
    }

    fun Search(text: String?) {
        mSocket?.emit("search", text)
    }

    fun SetSavedMappingsListener(listener: Emitter.Listener?) {
        SavedMappingsListener = listener
        mSocket?.on("getUserSearchesList", SavedMappingsListener)
    }

    fun GetSavedMappings(username: String?) {
        mSocket?.emit("getUserSearchesList", username)
    }

    fun GetFullNotificationsList(username: String?) {
        mSocket?.emit("notifications", username)
    }

    fun ChangeNotificationsStatus(
        username: String?,
        notifications: JSONArray?
    ) {
        mSocket?.emit("changeNotificationStatus", username, notifications)
    }

    fun SetUnavailableTimeAnswer(listener: Emitter.Listener?) {
        UnavailableTimeAnswerListener = listener
        mSocket?.on("unavailableTime", UnavailableTimeAnswerListener)
    }

    fun GetUserUnavailableTime(username: String?) {
        mSocket?.emit("unavailableTime", username)
    }

    fun SetUserUnavailableTime(
        username: String?,
        unavailableTime: JSONObject?
    ) {
        mSocket?.emit("unavailableTime", username, unavailableTime)
    }

    fun SetMappingAnswerListener(listener: Emitter.Listener?) {
        MappingAnswerListener = listener
        mSocket?.on("mapping", MappingAnswerListener)
    }

    fun GetMappingsResult(data: JSONObject?, username: String?) {
        mSocket?.emit("mapping", data, username)
    }

    fun SetUserSubscribeListener(listener: Emitter.Listener?) {
        UserSubscribeListener = listener
        mSocket?.on("subscribe", UserSubscribeListener)
    }

    fun SubscribeOnUser(
        receiver: String?,
        subscriber: String?,
        direction: Boolean?
    ) {
        mSocket?.emit("subscribe", receiver, subscriber, direction)
    }

    fun SetUserTaskSubscribeListener(listener: Emitter.Listener?) {
        UserTaskSubscribeListener = listener
        mSocket?.on("subscribe", UserTaskSubscribeListener)
    }

    fun SubscribeOnUserTask(
        receiver: String?,
        subscriber: String?,
        direction: Boolean?,
        taskId: Int
    ) {
        mSocket?.emit("subscribe", receiver, subscriber, direction, taskId)
    }


    fun SetNotificationsReceiver(username: String?) {
        CurrentUser = username
        mSocket?.on("changes_$CurrentUser", NotificationsListener)
    }

    fun SetNotificationsListener(listener: Emitter.Listener?) {
        if (listener == null) {
            mSocket?.off("changes_$CurrentUser", NotificationsListener)
            NotificationsListener = DefaultNotificationsListener
            mSocket?.on("changes_$CurrentUser", NotificationsListener)
        } else {
            mSocket?.off("changes_$CurrentUser", NotificationsListener)
            NotificationsListener = listener
            mSocket?.on("changes_$CurrentUser", NotificationsListener)
        }
    }


    fun SetFullNotificationsListListener(listener: Emitter.Listener?) {
        if (listener == null) {
            mSocket?.off("notifications", FullNotificationsListListener)
            FullNotificationsListListener = DefaultFullNotificationsListListener
            mSocket?.on("notifications", FullNotificationsListListener)
        } else {
            mSocket?.off("notifications", FullNotificationsListListener)
            FullNotificationsListListener = listener
            mSocket?.on("notifications", FullNotificationsListListener)
        }
    }
}