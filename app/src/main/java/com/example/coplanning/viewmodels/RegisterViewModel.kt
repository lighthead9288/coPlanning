package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.coplanning.communication.ICoPlanningAPI
import com.example.coplanning.communication.RetrofitClient
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.models.ServerTask
import com.example.coplanning.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(val application: Application, val onRegisterCallback: ()->Unit): ViewModel() {

    private val socketClient: SocketClient = SocketClient()

    private var sharedPrefs = SharedPreferencesOperations(application)

    private lateinit var register: Call<User>

    var name: String = ""
    var surname: String = ""
    var username: String = ""
    var password: String = ""

    fun performRegister() {

        val rClient = RetrofitClient()
        val retrofit = rClient.GetRetrofitEntity()
        val client: ICoPlanningAPI = retrofit!!.create(ICoPlanningAPI::class.java)

        register = client.register(User(name = name, surname = surname, username = username, password = password,
            taskList = mutableListOf(), subscriberList = mutableListOf()))

        register.enqueue(object : Callback<User?> {
            override fun onResponse(
                call: Call<User?>,
                response: Response<User?>
            ) {
                val user = response.body()
                if (user != null) {
                    val login: String = user.name
                    val taskList = user.taskList

                    sharedPrefs?.ClearSharedPreferences()
                    sharedPrefs?.SetSharedPreferences(username, password)

                    socketClient.Connect()
                    socketClient.SetNotificationsListener(null)
                    socketClient.SetFullNotificationsListListener(null)
                    socketClient.SetNotificationsReceiver(login)

                    onRegisterCallback()

                }
            }

            override fun onFailure(call: Call<User?>?, t: Throwable?) {
            }
        })
    }
}