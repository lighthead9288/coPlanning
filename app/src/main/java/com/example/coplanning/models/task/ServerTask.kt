package com.example.coplanning.models.task

import com.example.coplanning.models.task.Task
import java.util.*

open class ServerTask : Task {
    private var subscriberList: ArrayList<String>? = null

    private var taskNumber = 0

    constructor(name: String?): super(name)

    constructor(task: Task) : super(task)

    fun setSubscriberList(list: ArrayList<String>?) {
        subscriberList = list
    }

    fun getSubscriberList(): ArrayList<String>? {
        return subscriberList
    }

    fun setTaskNumber(number: Int) {
        taskNumber = number
    }

    fun getTaskNumber(): Int {
        return taskNumber
    }



}