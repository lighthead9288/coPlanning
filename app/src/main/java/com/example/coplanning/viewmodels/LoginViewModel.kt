package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.coplanning.communication.ICoPlanningAPI
import com.example.coplanning.communication.RetrofitClient
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.BadgesOperations
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(val application: Application, val onLoginCallback: ()->Unit): ViewModel() {

    private lateinit var login: Call<User>
    private val socketClient: SocketClient = SocketClient()

    private var sharedPrefs = SharedPreferencesOperations(application)

    var username: String = ""
    var password: String = ""

    fun performLogin() {

        val rClient = RetrofitClient()
        val retrofit = rClient.GetRetrofitEntity()
        val client: ICoPlanningAPI = retrofit!!.create(ICoPlanningAPI::class.java)
        login = client.login(username, password)
        login.enqueue(object : Callback<User?> {
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

                            onLoginCallback()

                    }
                }

                override fun onFailure(call: Call<User?>?, t: Throwable?) {
                }
        })
    }



}