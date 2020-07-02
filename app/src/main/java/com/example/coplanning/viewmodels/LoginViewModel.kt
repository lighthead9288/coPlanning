package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.coplanning.communication.ICoPlanningAPI
import com.example.coplanning.communication.RetrofitClient
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.models.user.User
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
        val retrofit = rClient.getRetrofitEntity()
        val client: ICoPlanningAPI = retrofit.create(ICoPlanningAPI::class.java)
        login = client.login(username, password)
        login.enqueue(object : Callback<User?> {
                override fun onResponse(
                    call: Call<User?>,
                    response: Response<User?>
                    ) {
                        val user = response.body()
                        if (user != null) {
                            val login: String = user.name
                            sharedPrefs.clearSharedPreferences()
                            sharedPrefs.setSharedPreferences(username, password)

                            socketClient.connect()
                            socketClient.setNotificationsListener(null)
                            socketClient.setFullNotificationsListListener(null)
                            socketClient.setNotificationsReceiver(login)

                            onLoginCallback()
                    }
                }

                override fun onFailure(call: Call<User?>?, t: Throwable?) {
                }
        })
    }



}