package com.example.coplanning.globals

import android.app.Application
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView

open class MenuOperations: Application() {

    companion object {
        var menu: BottomNavigationView? = null
    }

    fun SetMenu(bnv: BottomNavigationView?) {
        menu = bnv
        
    }

    fun ShowBottomNavigationView() {
        if (menu != null) {
            menu?.visibility = View.VISIBLE
        }
    }

    fun HideBottomNavigationView() {
        if (menu!= null) {
            menu?.visibility = View.GONE
        }
    }
}