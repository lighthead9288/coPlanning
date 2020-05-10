package com.example.coplanning.models

import com.example.coplanning.helpers.DateAndTimeConverter
import java.io.Serializable

abstract class Task: Serializable {

    private var name: String? = null

    private var comment: String? = null

    protected var dateFrom: String? = null

    protected var timeFrom: String? = null

    protected var dateTo: String? = null

    protected var timeTo: String? = null

    private var visibility = false

    private var editable = false

    private var completed = false

    open fun GetName(): String? {
        return name
    }

    open fun GetComment(): String? {
        return comment
    }

    open fun GetDateFrom(): String? {
        return dateFrom
    }

    open fun GetTimeFrom(): String? {
        return timeFrom
    }

    open fun GetDateTo(): String? {
        return dateTo
    }

    open fun GetTimeTo(): String? {
        return timeTo
    }


    open fun GetVisibility(): Boolean? {
        return visibility
    }

    open fun GetEditable(): Boolean? {
        return editable
    }

    open fun GetCompleted(): Boolean? {
        return completed
    }

    open fun SetName(inpName: String?) {
        name = inpName
    }

    open fun SetComment(inpComment: String?) {
        comment = inpComment
    }

    open fun SetDateFrom(year: Int, month: Int, date: Int) {
        dateFrom = DateAndTimeConverter.ConvertToISOStringDate(year, month, date)
    }

    open fun SetTimeFrom(hours: Int, minutes: Int) {
        timeFrom = DateAndTimeConverter.ConvertToISOStringTime(hours, minutes)
    }

    open fun SetDateTo(year: Int, month: Int, date: Int) {
        dateTo = DateAndTimeConverter.ConvertToISOStringDate(year, month, date)
    }

    open fun SetTimeTo(hours: Int, minutes: Int) {
        timeTo = DateAndTimeConverter.ConvertToISOStringTime(hours, minutes)
    }


    open fun SetVisibility(inpVisibility: Boolean) {
        visibility = inpVisibility
    }

    open fun SetEditable(inpEditable: Boolean) {
        editable = inpEditable
    }

    open fun SetCompleted(inpCompleted: Boolean) {
        completed = inpCompleted
    }


    constructor(name: String?) {
        SetName(name)
    }

    constructor(task: Task) {
        SetName(task.GetName())
        SetComment(task.GetComment())
        val strDateFrom: String? = task.GetDateFrom()
        if (strDateFrom != null && strDateFrom !== "") SetDateFrom(
            DateAndTimeConverter.GetYearFromISOStringDate(
                strDateFrom
            ),
            DateAndTimeConverter.GetMonthFromISOStringDate(strDateFrom),
            DateAndTimeConverter.GetDayFromISOStringDate(strDateFrom)
        )
        val strTimeFrom: String? = task.GetTimeFrom()
        if (strTimeFrom != null && strTimeFrom !== "") SetTimeFrom(
            DateAndTimeConverter.GetHourFromISOStringTime(
                strTimeFrom
            ), DateAndTimeConverter.GetMinutesFromISOStringTime(strTimeFrom)
        )
        val strDateTo: String? = task.GetDateTo()
        if (strDateTo != null && strDateTo !== "") SetDateTo(
            DateAndTimeConverter.GetYearFromISOStringDate(
                strDateTo
            ),
            DateAndTimeConverter.GetMonthFromISOStringDate(strDateTo),
            DateAndTimeConverter.GetDayFromISOStringDate(strDateTo)
        )
        val strTimeTo: String? = task.GetTimeTo()
        if (strTimeTo != null && strTimeTo !== "") SetTimeTo(
            DateAndTimeConverter.GetHourFromISOStringTime(
                strTimeTo
            ), DateAndTimeConverter.GetMinutesFromISOStringTime(strTimeTo)
        )
        task?.GetVisibility()?.let { SetVisibility(it) }
        task?.GetEditable()?.let { SetEditable(it) }
        task?.GetCompleted()?.let { SetCompleted(it) }
    }
}