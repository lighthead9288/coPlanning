package com.example.coplanning.models

import com.example.coplanning.helpers.DateAndTimeConverter
import java.util.*

open class ServerTask: Task {
    private var subscriberList: ArrayList<String>? = null

    var taskNumber = 0

    constructor(name: String?): super(name) {

    }

    constructor(task: Task) : super(task) {
        if (task.GetDateFrom() == null) dateFrom = ""
        if (task.GetTimeFrom() == null) timeFrom = ""
        if (task.GetDateTo() == null) dateTo = ""
        if (task.GetTimeTo() == null) timeTo = ""
    }

    fun SetSubscriberList(list: ArrayList<String>?) {
        subscriberList = list
    }

    fun GetSubscriberList(): ArrayList<String>? {
        return subscriberList
    }

    fun SetTaskNumber(number: Int) {
        taskNumber = number
    }

    fun GetTaskNumber(): Int {
        return taskNumber
    }

    override fun SetDateFrom(year: Int, month: Int, date: Int) {
        dateFrom = DateAndTimeConverter.ConvertToISOStringDate(year, month, date)
    }

    override fun SetTimeFrom(hours: Int, minutes: Int) {
        timeFrom = DateAndTimeConverter.ConvertToISOStringTime(hours, minutes)
    }

    override fun SetDateTo(year: Int, month: Int, date: Int) {
        dateTo = DateAndTimeConverter.ConvertToISOStringDate(year, month, date)
    }

    override fun SetTimeTo(hours: Int, minutes: Int) {
        timeTo = DateAndTimeConverter.ConvertToISOStringTime(hours, minutes)
    }

}