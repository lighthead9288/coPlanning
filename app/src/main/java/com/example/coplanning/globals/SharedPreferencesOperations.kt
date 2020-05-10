package com.example.coplanning.globals

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesOperations(context: Context) {
    val PREFS_FILENAME = "com.example.coplanning.prefs"
    val sPref: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);

    var login: String?
        get() = sPref.getString("login", "")
        set(value) = sPref.edit().putString("login", value).apply()
    var password: String?
        get() = sPref.getString("password", "")
        set(value) = sPref.edit().putString("password", value).apply()

    fun SetSharedPreferences(login: String?, password: String?) {
        sPref.edit().putString("login", login).apply()
        sPref.edit().putString("password", password).apply()
    }

    fun ClearSharedPreferences() {
        sPref.edit().putString("login", "").apply()
        sPref.edit().putString("password", "").apply()

    }


}