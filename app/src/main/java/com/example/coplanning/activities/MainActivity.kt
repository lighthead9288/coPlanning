package com.example.coplanning.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.coplanning.R
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.fragments.*
import com.example.coplanning.globals.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navigation: BottomNavigationView
    private val fragmentOperations: FragmentOperations = FragmentOperations(supportFragmentManager)
    private var menuOperations = MenuOperations()
    private lateinit var sharedPrefs: SharedPreferencesOperations
    private var badgesOperations = BadgesOperations()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initMenu()
        initAccount()
    }

    private fun initMenu() {
        navigation = findViewById(R.id.bottom_navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        menuOperations.setMenu(navigation)
        badgesOperations.init(navigation, this)
    }

    private fun initAccount() {
        sharedPrefs = SharedPreferencesOperations(this)
        val login = sharedPrefs.login
        val password = sharedPrefs.password

        if ((login.isNullOrEmpty())&&(password.isNullOrEmpty())) {
            menuOperations.hideBottomNavigationView()
            fragmentOperations.loadFragment(LoginFragment())
        } else {
            fragmentOperations.loadFragment(ScheduleFragment())
            val socketClient = SocketClient(this, badgesOperations)
            socketClient.connect()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) finish()
    }


    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.mySchedule -> {
                        if (ScreensDataStorage.curScheduleScreenData!=null) {
                            fragmentOperations.loadFragment(ScreensDataStorage.curScheduleScreenData!!)
                        } else {
                            fragmentOperations.loadFragment(ScheduleFragment())
                        }

                        return true
                    }
                    R.id.search -> {
                        if (ScreensDataStorage.curSearchScreenData!=null) {
                            fragmentOperations.loadFragment(ScreensDataStorage.curSearchScreenData!!)
                        } else {
                            fragmentOperations.loadFragment(SearchFragment())
                        }
                        return true
                    }
                    R.id.mappings -> {
                        if (ScreensDataStorage.curMappingsScreenData!=null) {
                            fragmentOperations.loadFragment(ScreensDataStorage.curMappingsScreenData!!)
                        } else {
                            fragmentOperations.loadFragment(MappingsFragment())
                        }
                        return true
                    }
                    R.id.notifications -> {
                        if (ScreensDataStorage.curNotificationsScreenData!=null) {
                            fragmentOperations.loadFragment(ScreensDataStorage.curNotificationsScreenData!!)
                        } else {
                            fragmentOperations.loadFragment(NotificationsFragment())
                        }
                        return true
                    }
                    R.id.profile -> {
                        if (ScreensDataStorage.curProfileScreenData!=null) {
                            fragmentOperations.loadFragment(ScreensDataStorage.curProfileScreenData!!)
                        } else {
                            fragmentOperations.loadFragment(ProfileFragment())
                        }
                        return true
                    }
                }
                return true
            }
        }
}
