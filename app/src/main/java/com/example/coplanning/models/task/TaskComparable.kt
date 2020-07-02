package com.example.coplanning.models.task

import com.example.coplanning.helpers.DateAndTimeConverter
import java.util.*

class TaskComparable(task: ServerTask) : ServerTask(task) {
    private val dateTimeFrom = Calendar.getInstance()
    private val dateTimeTo = Calendar.getInstance()

    init {
        setTaskNumber(task.getTaskNumber())
        val taskStrDateFrom = task.getDateFrom()
        val taskStrTimeFrom = task.getTimeFrom()
        if (!taskStrDateFrom.isNullOrEmpty()) {
            val taskIntYear: Int = DateAndTimeConverter.getYearFromISOStringDate(taskStrDateFrom)
            val taskIntMonth: Int = DateAndTimeConverter.getMonthFromISOStringDate(taskStrDateFrom)
            val taskIntDate: Int = DateAndTimeConverter.getDayFromISOStringDate(taskStrDateFrom)
            var taskIntHours = 0
            var taskIntMinutes = 0
            if (!taskStrTimeFrom.isNullOrEmpty()) {
                taskIntHours = DateAndTimeConverter.getHourFromISOStringTime(taskStrTimeFrom)
                taskIntMinutes = DateAndTimeConverter.getMinutesFromISOStringTime(taskStrTimeFrom)
            }
            setDateTimeFrom(
                taskIntYear,
                taskIntMonth,
                taskIntDate,
                taskIntHours,
                taskIntMinutes
            )
        }
        val taskStrDateTo = task.getDateTo()
        val taskStrTimeTo = task.getTimeTo()
        if (!taskStrDateTo.isNullOrEmpty()) {
            val taskIntYear: Int = DateAndTimeConverter.getYearFromISOStringDate(taskStrDateTo)
            val taskIntMonth: Int = DateAndTimeConverter.getMonthFromISOStringDate(taskStrDateTo)
            val taskIntDate: Int = DateAndTimeConverter.getDayFromISOStringDate(taskStrDateTo)
            var taskIntHours = 0
            var taskIntMinutes = 0
            if (!taskStrTimeTo.isNullOrEmpty()) {
                taskIntHours = DateAndTimeConverter.getHourFromISOStringTime(taskStrTimeTo)
                taskIntMinutes = DateAndTimeConverter.getMinutesFromISOStringTime(taskStrTimeTo)
            }
            setDateTimeTo(taskIntYear, taskIntMonth, taskIntDate, taskIntHours, taskIntMinutes)
        }
        setSubscriberList(task.getSubscriberList())
    }

    fun setDateTimeFrom(year: Int, month: Int, date: Int, hours: Int, minutes: Int) {
        dateTimeFrom[year, month - 1, date, hours] = minutes
    }

    fun getDateTimeFrom(): Calendar {
        return dateTimeFrom
    }

    fun setDateTimeTo(year: Int, month: Int, date: Int, hours: Int, minutes: Int) {
        dateTimeTo[year, month - 1, date, hours] = minutes
    }

    fun getDateTimeTo(): Calendar {
        return dateTimeTo
    }

    companion object {
        var dateTimeFromComparator =
            Comparator<TaskComparable> { o1, o2 ->
                val calendarTask1 = o1.getDateTimeFrom()
                val calendarTask2 = o2.getDateTimeFrom()
                calendarTask1.compareTo(calendarTask2)
            }
    }

}