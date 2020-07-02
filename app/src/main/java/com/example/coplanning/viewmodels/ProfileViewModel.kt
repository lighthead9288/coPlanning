package com.example.coplanning.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.BadgesOperations
import com.example.coplanning.globals.MappingElementsManager
import com.example.coplanning.globals.ScreensDataStorage
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.helpers.DateAndTimeConverter
import com.github.nkzawa.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ProfileViewModel(val application: Application) {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var sharedPrefs = SharedPreferencesOperations(application)

    private val socketClient: SocketClient = SocketClient()

    private val badgesOperations: BadgesOperations = BadgesOperations()

    private val mappingElementsManager: MappingElementsManager = MappingElementsManager()

    private val _timeFromString = MutableLiveData<String>()
    val timeFromString: LiveData<String>
        get() = _timeFromString

    private val _timeToString = MutableLiveData<String>()
    val timeToString: LiveData<String>
        get() = _timeToString

    private val _curUserName = MutableLiveData<String>()
    val curUserName: LiveData<String>
        get() = _curUserName


    private val onUnavailableTimeAnswer = Emitter.Listener { args->
        uiScope.launch {

            val data = args[0] as JSONObject
            try {
                val defaultUnavailableTime = data.getJSONObject("default")
                _timeFromString.value = defaultUnavailableTime.getString("from")
                _timeToString.value = defaultUnavailableTime.getString("to")

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    init {

        _timeFromString.value = "00:00"
        _timeToString.value = "00:00"

        val username = getCurUserName()
        _curUserName.value = username

        socketClient.setUnavailableTimeAnswer(onUnavailableTimeAnswer)
        socketClient.getUserUnavailableTime(username)
    }

    fun logout() {
        badgesOperations.clearMappingsAmount()
        sharedPrefs.clearSharedPreferences()

        mappingElementsManager.clearMappingElements()
        socketClient.offListeners()
        socketClient.disconnect()

        ScreensDataStorage.clearAll()
    }

    fun SaveIntervals() {
        val unavailableTime = JSONObject()
        val defaultUnavailableTime = JSONObject()

        defaultUnavailableTime.put("from", _timeFromString.value)
        defaultUnavailableTime.put("to", _timeToString.value)

        val customIntervals = JSONArray()

        unavailableTime.put("default", defaultUnavailableTime)
        unavailableTime.put("custom", customIntervals)

        val username = getCurUserName()
        socketClient.setUserUnavailableTime(username, unavailableTime)

        uiScope.launch {
            Toast.makeText(application, "Interval has been saved!", Toast.LENGTH_LONG).show()
        }
    }


    private fun getCurUserName(): String? { return sharedPrefs.login}


    fun setTimeFromCommand(hours: Int, minutes: Int) {
        _timeFromString.value = DateAndTimeConverter.convertToISOStringTime(hours, minutes)
    }

    fun setTimeToCommand(hours: Int, minutes: Int) {
        _timeToString.value = DateAndTimeConverter.convertToISOStringTime(hours, minutes)
    }



}