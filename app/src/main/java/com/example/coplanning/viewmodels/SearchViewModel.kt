package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.BadgesOperations
import com.example.coplanning.globals.MappingElementsManager
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.models.User
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

    val mappingElementsManager: MappingElementsManager = MappingElementsManager()

    private val badgesOperations: BadgesOperations = BadgesOperations()


    val _usersList = MutableLiveData<List<User>>()
    val usersList: LiveData<List<User>>
        get() = _usersList

    var query = ""

    fun GetCurUserName(): String? { return sharedPrefs.login}


    fun AddUserToMapping(username: String, listener: MappingAddChangeViewListener) {
        var curMappingElements = mappingElementsManager.GetMappingElements()
        if (!curMappingElements.contains(username))
            mappingElementsManager.AddMappingElement(username)
        else
            mappingElementsManager.RemoveMappingElement(username)

        curMappingElements = mappingElementsManager.GetMappingElements()
        badgesOperations.SetMappingsAmount(curMappingElements.count())
       // SearchCommand(query)
        listener.onMappingAdd(username)
    }
    fun SearchCommand(searchString: String) {
        query = searchString
        if (!searchString.isNullOrEmpty())
            socketClient.Search(searchString)
    }

    fun Subscribe(user: User, direction: Boolean, listener: SubscribeChangeViewListener) {
        curUser = user
        onSubscribeChangeViewListener = listener

        socketClient.SubscribeOnUser(user.username, GetCurUserName(), direction)
        socketClient.SetUserSubscribeListener(onSubscribeAnswer)
    }

    private var curUser: User? = null
    private var onSubscribeChangeViewListener: SubscribeChangeViewListener? = null
    private val onSubscribeAnswer = Emitter.Listener {args ->
        uiScope.launch {
            //SearchCommand(query)
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
            val usersList: ArrayList<User> = ArrayList<User>()
            for (i in 0 until data.length()) {
               // var jsonobject: JSONObject
                try {
                 //   jsonobject = data.getJSONObject(i)
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
        socketClient.SetSearchAnswerListener(onSearchAnswer)
    }

}

class MappingAddChangeViewListener(val listener: (username: String) -> Unit) {
    fun onMappingAdd(username: String) = listener(username)
}

class SubscribeChangeViewListener(val listener: (user: User?) -> Unit) {
    fun onSubscribeChange(user: User?) = listener(user)
}