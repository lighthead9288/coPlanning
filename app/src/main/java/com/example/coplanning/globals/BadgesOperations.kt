package com.example.coplanning.globals

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.coplanning.R
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class BadgesOperations {

    companion object {
        private var navigation:BottomNavigationView? = null
        private var MainActivityContext: Context? = null
        private var mappingBadge: View? = null
        private var mappingText: TextView? = null
        private var notificationBadge: View? = null
        private var notificationText: TextView? = null
    }

    fun Init(view: BottomNavigationView?, context: Context?) {
        navigation = view
        MainActivityContext = context
        val bottomNavigationMenuView: BottomNavigationMenuView =
            navigation?.getChildAt(0) as BottomNavigationMenuView
        val mappingItemView: BottomNavigationItemView =
            bottomNavigationMenuView.getChildAt(2) as BottomNavigationItemView
        mappingBadge = LayoutInflater.from(MainActivityContext)
            .inflate(R.layout.notification_badge, mappingItemView, true)
        mappingText = mappingBadge!!.findViewById(R.id.notifications_badge)
        mappingText?.setVisibility(View.GONE)
        val notificationItemView: BottomNavigationItemView =
            bottomNavigationMenuView.getChildAt(3) as BottomNavigationItemView
        notificationBadge = LayoutInflater.from(MainActivityContext)
            .inflate(R.layout.notification_badge, notificationItemView, true)
        notificationText = notificationBadge!!.findViewById(R.id.notifications_badge)
        notificationText?.visibility = View.GONE
    }


    fun SetMappingsAmount(amount: Int) {
        if (amount == 0)
            mappingText!!.visibility = View.GONE
        else
            mappingText!!.visibility = View.VISIBLE
        mappingText!!.text = amount.toString()
    }

    fun ClearMappingsAmount() {
        mappingText = mappingBadge!!.findViewById(R.id.notifications_badge)
        mappingText?.setText("0")
        mappingText?.setVisibility(View.GONE)
    }

    fun SetNotificationsAmount(amount: Int) {
        if (amount == 0)
            notificationText!!.visibility = View.GONE
        else
            notificationText!!.visibility = View.VISIBLE
        notificationText!!.text = amount.toString()
    }

    fun ClearNotificationsAmount() {
        notificationText = notificationBadge!!.findViewById(R.id.notifications_badge)
        notificationText?.setText("0")
        notificationText?.setVisibility(View.GONE)
    }

    fun GetNotificationsAmount(): Int {
        val str = notificationText!!.text.toString()
        return if (str == "") 0 else Integer.valueOf(notificationText!!.text.toString())
    }
}