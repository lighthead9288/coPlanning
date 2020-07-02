package com.example.coplanning.models.task

import com.example.coplanning.helpers.DateAndTimeConverter
import java.io.Serializable

abstract class Task : Serializable {

    private var name: String? = null

    private var comment: String? = null

    private var dateFrom: String? = ""

    private var timeFrom: String? = ""

    private var dateTo: String? = ""

    private var timeTo: String? = ""

    private var visibility = false

    private var editable = false

    private var completed = false

    constructor(name: String?) {
        setName(name)
    }

    constructor(task: Task) {
        setName(task.getName())
        setComment(task.getComment())
        val strDateFrom: String? = task.getDateFrom()
        if (!strDateFrom.isNullOrEmpty()) {
            setDateFrom(
                DateAndTimeConverter.getYearFromISOStringDate(
                    strDateFrom
                ),
                DateAndTimeConverter.getMonthFromISOStringDate(strDateFrom),
                DateAndTimeConverter.getDayFromISOStringDate(strDateFrom)
            )
        }
        val strTimeFrom: String? = task.getTimeFrom()
        if (!strTimeFrom.isNullOrEmpty()) {
            setTimeFrom(
                DateAndTimeConverter.getHourFromISOStringTime(
                    strTimeFrom
                ),
                DateAndTimeConverter.getMinutesFromISOStringTime(strTimeFrom)
            )
        }
        val strDateTo: String? = task.getDateTo()
        if (!strDateTo.isNullOrEmpty()) {
            setDateTo(
                DateAndTimeConverter.getYearFromISOStringDate(
                    strDateTo
                ),
                DateAndTimeConverter.getMonthFromISOStringDate(strDateTo),
                DateAndTimeConverter.getDayFromISOStringDate(strDateTo)
            )
        }
        val strTimeTo: String? = task.getTimeTo()
        if (!strTimeTo.isNullOrEmpty()) {
            setTimeTo(
                DateAndTimeConverter.getHourFromISOStringTime(
                    strTimeTo
                ),
                DateAndTimeConverter.getMinutesFromISOStringTime(strTimeTo)
            )
        }
        task.getVisibility()?.let { setVisibility(it) }
        task.getEditable()?.let { setEditable(it) }
        task.getCompleted()?.let { setCompleted(it) }
    }

    open fun getName(): String? {
        return name
    }

    open fun getComment(): String? {
        return comment
    }

    open fun getDateFrom(): String? {
        return dateFrom
    }

    open fun getTimeFrom(): String? {
        return timeFrom
    }

    open fun getDateTo(): String? {
        return dateTo
    }

    open fun getTimeTo(): String? {
        return timeTo
    }


    open fun getVisibility(): Boolean? {
        return visibility
    }

    open fun getEditable(): Boolean? {
        return editable
    }

    open fun getCompleted(): Boolean? {
        return completed
    }

    open fun setName(inpName: String?) {
        name = inpName
    }

    open fun setComment(inpComment: String?) {
        comment = inpComment
    }

    open fun setDateFrom(year: Int, month: Int, date: Int) {
        dateFrom = DateAndTimeConverter.convertToISOStringDate(year, month, date)
    }

    fun setTimeFrom(hours: Int, minutes: Int) {
        timeFrom = DateAndTimeConverter.convertToISOStringTime(hours, minutes)
    }

    open fun setDateTo(year: Int, month: Int, date: Int) {
        dateTo = DateAndTimeConverter.convertToISOStringDate(year, month, date)
    }

    open fun setTimeTo(hours: Int, minutes: Int) {
        timeTo = DateAndTimeConverter.convertToISOStringTime(hours, minutes)
    }


    open fun setVisibility(inpVisibility: Boolean) {
        visibility = inpVisibility
    }

    open fun setEditable(inpEditable: Boolean) {
        editable = inpEditable
    }

    open fun setCompleted(inpCompleted: Boolean) {
        completed = inpCompleted
    }



}