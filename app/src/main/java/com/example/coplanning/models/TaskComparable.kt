package com.example.coplanning.models

import com.example.coplanning.helpers.DateAndTimeConverter
import java.util.*

class TaskComparable(task: ServerTask) : ServerTask(task) {
    private val DateTimeFrom = Calendar.getInstance()
    private val DateTimeTo = Calendar.getInstance()
    fun SetDateTimeFrom(year: Int, month: Int, date: Int, hours: Int, minutes: Int) {
        DateTimeFrom[year, month - 1, date, hours] = minutes
    }

    fun GetDateTimeFrom(): Calendar {
        return DateTimeFrom
    }

    fun SetDateTimeTo(year: Int, month: Int, date: Int, hours: Int, minutes: Int) {
        DateTimeTo[year, month - 1, date, hours] = minutes
    }

    fun GetDateTimeTo(): Calendar {
        return DateTimeTo
    }

    companion object {
        var DateTimeFromComparator =
            Comparator<TaskComparable> { o1, o2 ->
                val calendarTask1 = o1.GetDateTimeFrom()
                val calendarTask2 = o2.GetDateTimeFrom()
                calendarTask1.compareTo(calendarTask2)
            }
        var DateTimeToComparator =
            Comparator<TaskComparable> { o1, o2 ->
                val calendarTask1 = o1.GetDateTimeTo()
                val calendarTask2 = o2.GetDateTimeTo()
                calendarTask1.compareTo(calendarTask2)
            }
        var CompletedComparator =
            Comparator<TaskComparable> { o1, o2 ->
                val completed1 = o1.GetCompleted()!!
                val completed2 = o2.GetCompleted()!!
                val intCompleted1 = if (completed1) 1 else 0
                val intCompleted2 = if (completed2) 1 else 0
                intCompleted1 - intCompleted2
            }
    }

    init {
        SetTaskNumber(task.GetTaskNumber())
        val taskStrDateFrom = task.GetDateFrom()
        val taskStrTimeFrom = task.GetTimeFrom()
        if (taskStrDateFrom != null && taskStrDateFrom !== "") {
            val taskIntYear: Int = DateAndTimeConverter.GetYearFromISOStringDate(taskStrDateFrom)
            val taskIntMonth: Int = DateAndTimeConverter.GetMonthFromISOStringDate(taskStrDateFrom)
            val taskIntDate: Int = DateAndTimeConverter.GetDayFromISOStringDate(taskStrDateFrom)
            var taskIntHours = 0
            var taskIntMinutes = 0
            if (taskStrTimeFrom != null && taskStrTimeFrom !== "") {
                taskIntHours = DateAndTimeConverter.GetHourFromISOStringTime(taskStrTimeFrom)
                taskIntMinutes = DateAndTimeConverter.GetMinutesFromISOStringTime(taskStrTimeFrom)
            }
            SetDateTimeFrom(
                taskIntYear,
                taskIntMonth,
                taskIntDate,
                taskIntHours,
                taskIntMinutes
            )
        }
        val taskStrDateTo = task.GetDateTo()
        val taskStrTimeTo = task.GetTimeTo()
        if (taskStrDateTo != null && taskStrDateTo !== "") {
            val taskIntYear: Int = DateAndTimeConverter.GetYearFromISOStringDate(taskStrDateTo)
            val taskIntMonth: Int = DateAndTimeConverter.GetMonthFromISOStringDate(taskStrDateTo)
            val taskIntDate: Int = DateAndTimeConverter.GetDayFromISOStringDate(taskStrDateTo)
            var taskIntHours = 0
            var taskIntMinutes = 0
            if (taskStrTimeTo != null && taskStrTimeTo !== "") {
                taskIntHours = DateAndTimeConverter.GetHourFromISOStringTime(taskStrTimeTo)
                taskIntMinutes = DateAndTimeConverter.GetMinutesFromISOStringTime(taskStrTimeTo)
            }
            SetDateTimeTo(taskIntYear, taskIntMonth, taskIntDate, taskIntHours, taskIntMinutes)
        }
        SetSubscriberList(task.GetSubscriberList())
    }
}