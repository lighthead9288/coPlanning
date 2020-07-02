package com.example.coplanning.globals

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.coplanning.R

class FragmentOperations: Application {
    private var supportFragmentManager: FragmentManager? = null

    constructor(supportFragmentManager: FragmentManager?) {
        this.supportFragmentManager = supportFragmentManager
    }

    fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager!!.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun clearBackStack() {
        if (supportFragmentManager!!.backStackEntryCount > 0) {
            val first: FragmentManager.BackStackEntry =
                supportFragmentManager!!.getBackStackEntryAt(0)
            supportFragmentManager!!.popBackStack(
                first.id,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }
}

