package com.example.coplanning.communication

import android.app.Activity
import android.app.Application
import com.example.coplanning.globals.BadgesOperations
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class SocketClient(): Application() {

    constructor(act: Activity?, ops: BadgesOperations?): this() {
        activity = act
        badgesOperations = ops
    }

    private var activity: Activity? = null

    private var badgesOperations: BadgesOperations? = null

    private var mSocket: com.github.nkzawa.socketio.client.Socket? =
        IO.socket("http://10.0.2.2:3000/")
       // IO.socket("https://co-planning.herokuapp.com/")

    private var searchAnswerListener: Emitter.Listener? = null
    private var savedMappingsListener: Emitter.Listener? = null
    private var unavailableTimeAnswerListener: Emitter.Listener? = null
    private var mappingAnswerListener: Emitter.Listener? = null
    private var userSubscribeListener: Emitter.Listener? = null
    private var userTaskSubscribeListener: Emitter.Listener? = null

    private var notificationsListener: Emitter.Listener? = null
    private var fullNotificationsListListener: Emitter.Listener? = null
    private var currentUser: String? = null

    private val defaultNotificationsListener: Emitter.Listener = Emitter.Listener {
        activity!!.runOnUiThread {
            val notifAmount: Int = badgesOperations?.getNotificationsAmount()!! + 1
            badgesOperations?.setNotificationsAmount(notifAmount)
        }
    }

    private val defaultFullNotificationsListListener: Emitter.Listener = Emitter.Listener { args ->
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
                badgesOperations?.setNotificationsAmount(unreadNotificationsAmount)
            }
        }

    fun connect() {
        mSocket?.connect()

    }

    fun disconnect() {
        mSocket?.disconnect()
    }

    fun offListeners() {
        mSocket?.off()
    }

    fun setSearchAnswerListener(listener: Emitter.Listener?) {
        searchAnswerListener = listener
        mSocket?.on("search", searchAnswerListener)
    }

    fun search(text: String?) {
        mSocket?.emit("search", text)
    }

    fun setSavedMappingsListener(listener: Emitter.Listener?) {
        savedMappingsListener = listener
        mSocket?.on("getUserSearchesList", savedMappingsListener)
    }

    fun getSavedMappings(username: String?) {
        mSocket?.emit("getUserSearchesList", username)
    }

    fun getFullNotificationsList(username: String?) {
        mSocket?.emit("notifications", username)
    }

    fun changeNotificationsStatus(
        username: String?,
        notifications: JSONArray?
    ) {
        mSocket?.emit("changeNotificationStatus", username, notifications)
    }

    fun setUnavailableTimeAnswer(listener: Emitter.Listener?) {
        unavailableTimeAnswerListener = listener
        mSocket?.on("unavailableTime", unavailableTimeAnswerListener)
    }

    fun getUserUnavailableTime(username: String?) {
        mSocket?.emit("unavailableTime", username)
    }

    fun setUserUnavailableTime(
        username: String?,
        unavailableTime: JSONObject?
    ) {
        mSocket?.emit("unavailableTime", username, unavailableTime)
    }

    fun setMappingAnswerListener(listener: Emitter.Listener?) {
        mappingAnswerListener = listener
        mSocket?.on("mapping", mappingAnswerListener)
    }

    fun getMappingsResult(data: JSONObject?, username: String?) {
        mSocket?.emit("mapping", data, username)
    }

    fun setUserSubscribeListener(listener: Emitter.Listener?) {
        userSubscribeListener = listener
        mSocket?.on("subscribe", userSubscribeListener)
    }

    fun subscribeOnUser(
        receiver: String?,
        subscriber: String?,
        direction: Boolean?
    ) {
        mSocket?.emit("subscribe", receiver, subscriber, direction)
    }

    fun setUserTaskSubscribeListener(listener: Emitter.Listener?) {
        userTaskSubscribeListener = listener
        mSocket?.on("subscribe", userTaskSubscribeListener)
    }

    fun subscribeOnUserTask(
        receiver: String?,
        subscriber: String?,
        direction: Boolean?,
        taskId: Int
    ) {
        mSocket?.emit("subscribe", receiver, subscriber, direction, taskId)
    }


    fun setNotificationsReceiver(username: String?) {
        currentUser = username
        mSocket?.on("changes_$currentUser", notificationsListener)
    }

    fun setNotificationsListener(listener: Emitter.Listener?) {
        if (listener == null) {
            mSocket?.off("changes_$currentUser", notificationsListener)
            notificationsListener = defaultNotificationsListener
            mSocket?.on("changes_$currentUser", notificationsListener)
        } else {
            mSocket?.off("changes_$currentUser", notificationsListener)
            notificationsListener = listener
            mSocket?.on("changes_$currentUser", notificationsListener)
        }
    }


    fun setFullNotificationsListListener(listener: Emitter.Listener?) {
        if (listener == null) {
            mSocket?.off("notifications", fullNotificationsListListener)
            fullNotificationsListListener = defaultFullNotificationsListListener
            mSocket?.on("notifications", fullNotificationsListListener)
        } else {
            mSocket?.off("notifications", fullNotificationsListListener)
            fullNotificationsListListener = listener
            mSocket?.on("notifications", fullNotificationsListListener)
        }
    }
}