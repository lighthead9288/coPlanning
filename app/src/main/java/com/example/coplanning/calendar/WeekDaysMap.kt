package com.example.coplanning.calendar

import com.example.coplanning.R
import java.util.*

class WeekDaysMap {

    companion object {
        private var weekDaysMap: MutableMap<Int, Int> = HashMap<Int, Int>()
        fun createMap(): Map<Int, Int>? {
           // weekDaysMap = HashMap<Int, Int>()
            weekDaysMap[0] = R.string.sun
            weekDaysMap[1] = R.string.mon
            weekDaysMap[2] = R.string.tue
            weekDaysMap[3] = R.string.wed
            weekDaysMap[4] = R.string.thu
            weekDaysMap[5] = R.string.fri
            weekDaysMap[6] = R.string.sat
            return weekDaysMap
        }
    }


}