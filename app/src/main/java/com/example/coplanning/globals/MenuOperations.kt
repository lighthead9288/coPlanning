package com.example.coplanning.globals

import android.app.Application
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView

open class MenuOperations: Application() {

    fun setMenu(bnv: BottomNavigationView?) {
        menu = bnv
    }

    fun showBottomNavigationView() {
        if (menu != null) {
            menu?.visibility = View.VISIBLE
        }
    }

    fun hideBottomNavigationView() {
        if (menu!= null) {
            menu?.visibility = View.GONE
        }
    }

    companion object {
        var menu: BottomNavigationView? = null
    }
}