package com.example.coplanning.models.mapping

import java.util.*

class MappingResultItem {

    private var _users: ArrayList<String>
    var users: ArrayList<String>
        get() = _users
        private set(value) {
            _users = value
        }

    private var _dateTimeFrom: Calendar
    var dateTimeFrom: Calendar
        get() = _dateTimeFrom
        private set(value) {
            _dateTimeFrom = value
        }

    private var _dateTimeTo: Calendar
    var dateTimeTo: Calendar
        get() = _dateTimeTo
        private set(value) {
            _dateTimeTo = value
        }

    private var _amount: Int
    var amount: Int
        get() = _amount
        private set(value) {
            _amount = value
        }

    constructor(users: ArrayList<String>, dateTimeFrom: Calendar, dateTimeTo: Calendar, amount: Int) {
        this._users = users
        this._dateTimeFrom = dateTimeFrom
        this._dateTimeTo = dateTimeTo
        if (amount>0) {
            this._amount = amount
        } else {
            this._amount = 0
        }
    }


    companion object {
        var dateTimeFromComparator =
            Comparator<MappingResultItem> { o1, o2 ->
                val calendarTask1 = o1.dateTimeFrom
                val calendarTask2 = o2.dateTimeTo
                calendarTask1.compareTo(calendarTask2)
            }
    }
}