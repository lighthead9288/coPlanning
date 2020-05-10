package com.example.coplanning

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
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

        navigation = findViewById(R.id.bottom_navigation)
        navigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

      /*  val badge = navigation.getOrCreateBadge(R.id.mySchedule)
        badge.isVisible = true
        badge.number = 15*/

        menuOperations.SetMenu(navigation)
        badgesOperations.Init(navigation, this)

        sharedPrefs = SharedPreferencesOperations(this)

        val login = sharedPrefs?.login
        val password = sharedPrefs?.password

        if ((login.isNullOrEmpty())&&(password.isNullOrEmpty())) {
            menuOperations.HideBottomNavigationView()
            fragmentOperations.LoadFragment(LoginFragment())
        }
        else {
            fragmentOperations.LoadFragment(ScheduleFragment())

            val socketClient = SocketClient(this, badgesOperations)
            socketClient.Connect()


           /* val socketClient = SocketClient(this, badgesOperations)
            socketClient.SetNotificationsListener(null)
            socketClient.Connect()*/
           /* socketClient.SetNotificationsReceiver(login)
            socketClient.SetFullNotificationsListListener(null)
            socketClient.GetFullNotificationsList(login)*/
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) finish()
    }

  /*  companion object {

        var curScheduleFragment: Fragment? = null
    }*/

    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {

                when (item.itemId) {
                    R.id.mySchedule -> {
                      /*  ScreensDataStorage.curScheduleScreenData?.let {
                            fragmentOperations.LoadFragment(it)
                        }*/
                        if (ScreensDataStorage.curScheduleScreenData!=null) {
                            fragmentOperations.LoadFragment(ScreensDataStorage.curScheduleScreenData!!)
                        }
                        else
                            fragmentOperations.LoadFragment(ScheduleFragment())

                        return true
                    }
                    R.id.search -> {
                       // fragmentOperations.LoadFragment(SearchFragment())
                        if (ScreensDataStorage.curSearchScreenData!=null)
                            fragmentOperations.LoadFragment(ScreensDataStorage.curSearchScreenData!!)
                        else
                            fragmentOperations.LoadFragment(SearchFragment())
                        return true
                    }
                    R.id.mappings -> {
                       // fragmentOperations.LoadFragment(MappingsFragment())
                        if (ScreensDataStorage.curMappingsScreenData!=null)
                            fragmentOperations.LoadFragment(ScreensDataStorage.curMappingsScreenData!!)
                        else
                            fragmentOperations.LoadFragment(MappingsFragment())
                        return true
                    }
                    R.id.notifications -> {
                      //  fragmentOperations.LoadFragment(NotificationsFragment())
                        if (ScreensDataStorage.curNotificationsScreenData!=null)
                            fragmentOperations.LoadFragment(ScreensDataStorage.curNotificationsScreenData!!)
                        else
                            fragmentOperations.LoadFragment(NotificationsFragment())
                        return true
                    }
                    R.id.profile -> {
                       // fragmentOperations.LoadFragment(ProfileFragment())
                        if (ScreensDataStorage.curProfileScreenData!=null)
                            fragmentOperations.LoadFragment(ScreensDataStorage.curProfileScreenData!!)
                        else
                            fragmentOperations.LoadFragment(ProfileFragment())
                        return true
                    }
                }
                return true
            }
        }
}
