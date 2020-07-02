package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.BadgesOperations
import com.example.coplanning.globals.MappingElementsManager
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.models.user.User
import com.github.nkzawa.emitter.Emitter
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class SearchViewModel(val application: Application): ViewModel() {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var sharedPrefs = SharedPreferencesOperations(application)

    private val socketClient: SocketClient = SocketClient()

    private val mappingElementsManager: MappingElementsManager = MappingElementsManager()

    private val badgesOperations: BadgesOperations = BadgesOperations()

    private val _usersList = MutableLiveData<List<User>>()
    val usersList: LiveData<List<User>>
        get() = _usersList

    private var query = ""

    fun getCurUserName(): String? { return sharedPrefs.login}


    fun addUserToMapping(username: String, listener: MappingAddChangeViewListener) {
        var curMappingElements = mappingElementsManager.getMappingElements()
        if (!curMappingElements.contains(username))
            mappingElementsManager.addMappingElement(username)
        else
            mappingElementsManager.removeMappingElement(username)

        curMappingElements = mappingElementsManager.getMappingElements()
        badgesOperations.setMappingsAmount(curMappingElements.count())
        listener.onMappingAdd(username)
    }
    fun searchCommand(searchString: String) {
        query = searchString
        socketClient.search(searchString)
    }

    fun subscribe(user: User, direction: Boolean, listener: SubscribeChangeViewListener) {
        curUser = user
        onSubscribeChangeViewListener = listener

        socketClient.subscribeOnUser(user.username, getCurUserName(), direction)
        socketClient.setUserSubscribeListener(onSubscribeAnswer)
    }

    private var curUser: User? = null
    private var onSubscribeChangeViewListener: SubscribeChangeViewListener? = null
    private val onSubscribeAnswer = Emitter.Listener {args ->
        uiScope.launch {
            try {
                val subscriber = args[1] as String
                val direction = args[2] as Boolean
                val subscriberList = curUser?.subscriberList?.toMutableList()
                if (direction)
                    subscriberList?.add(subscriber)
                else
                    subscriberList?.remove(subscriber)
                if (subscriberList != null) {
                    curUser?.subscriberList = subscriberList
                }

                onSubscribeChangeViewListener?.onSubscribeChange(curUser)
            }
            catch (e: Exception) {}

        }
    }

    //region Search results listener
    private val onSearchAnswer = Emitter.Listener { args ->
        uiScope.launch {
            val data = args[0] as JSONArray
            val usersList: ArrayList<User> = ArrayList()
            for (i in 0 until data.length()) {
                try {
                    val user: User = Gson().fromJson(data.getString(i), User::class.java)
                    usersList.add(user)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            _usersList.value = usersList
        }
    }

    init {
        socketClient.setSearchAnswerListener(onSearchAnswer)
    }

}

class MappingAddChangeViewListener(val listener: (username: String) -> Unit) {
    fun onMappingAdd(username: String) = listener(username)
}

class SubscribeChangeViewListener(val listener: (user: User?) -> Unit) {
    fun onSubscribeChange(user: User?) = listener(user)
}