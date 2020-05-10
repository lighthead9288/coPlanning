package com.example.coplanning.models

import java.util.*

class MappingResultItem {
    private var Users: ArrayList<String>? = null
    private var DateTimeFrom: Calendar? = null
    private var DateTimeTo: Calendar? = null
    private var Amount = 0
    fun SetUserList(users: ArrayList<String>?) {
        Users = users
    }

    fun SetDateTimeFrom(dateTimeFrom: Calendar?) {
        DateTimeFrom = dateTimeFrom
    }

    fun SetDateTimeTo(dateTimeTo: Calendar?) {
        DateTimeTo = dateTimeTo
    }

    fun SetAmount(amount: Int) {
        if (amount > 0) Amount = amount
    }

    fun GetUserList(): ArrayList<String>? {
        return Users
    }

    fun GetDateTimeFrom(): Calendar? {
        return DateTimeFrom
    }

    fun GetDateTimeTo(): Calendar? {
        return DateTimeTo
    }

    fun GetAmount(): Int {
        return Amount
    }

    companion object {
        var DateTimeFromComparator =
            Comparator<MappingResultItem> { o1, o2 ->
                val calendarTask1 = o1.GetDateTimeFrom()
                val calendarTask2 = o2.GetDateTimeFrom()
                calendarTask1!!.compareTo(calendarTask2)
            }
        var DateTimeToComparator =
            Comparator<MappingResultItem> { o1, o2 ->
                val calendarTask1 = o1.GetDateTimeTo()
                val calendarTask2 = o2.GetDateTimeTo()
                calendarTask1!!.compareTo(calendarTask2)
            }
    }
}