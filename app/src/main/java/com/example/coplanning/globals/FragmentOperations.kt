package com.example.coplanning.globals

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.coplanning.R

class FragmentOperations: Application {

    private var SupportFragmentManager: FragmentManager? = null


    constructor(supportFragmentManager: FragmentManager?) {
        SupportFragmentManager = supportFragmentManager
    }

    fun LoadFragment(fragment: Fragment) {
        // load fragment
        val transaction: FragmentTransaction = SupportFragmentManager!!.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun ClearBackStack() {
        if (SupportFragmentManager!!.getBackStackEntryCount() > 0) {
            val first: FragmentManager.BackStackEntry =
                SupportFragmentManager!!.getBackStackEntryAt(0)
            SupportFragmentManager!!.popBackStack(
                first.getId(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }
}

