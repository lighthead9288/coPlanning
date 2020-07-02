package com.example.coplanning.globals

import android.app.Application
import androidx.fragment.app.Fragment

class ScreensDataStorage : Application() {

    companion object {
        var curScheduleScreenData: Fragment? = null
        var curSearchScreenData: Fragment? = null
        var curMappingsScreenData: Fragment? = null
        var curNotificationsScreenData: Fragment? = null
        var curProfileScreenData: Fragment? = null

        fun clearAll() {
            curScheduleScreenData = null
            curSearchScreenData = null
            curMappingsScreenData = null
            curNotificationsScreenData = null
            curProfileScreenData = null

        }
    }


}