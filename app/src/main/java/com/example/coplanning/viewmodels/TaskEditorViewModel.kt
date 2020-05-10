package com.example.coplanning.viewmodels

import android.app.Application
import android.service.autofill.SaveCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.ITaskListOperations
import com.example.coplanning.models.ServerTask
import com.example.coplanning.models.ServerTaskManager
import com.example.coplanning.models.TaskComparable
import retrofit2.Converter
import java.util.*

class TaskEditorViewModel(val application: Application, val onSaveTaskCallback: ()->Unit): ViewModel(), ITaskListOperations {

    val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    val _comment = MutableLiveData<String>()
    val comment: LiveData<String>
        get() = _comment

    val _unspecifyDateFrom = MutableLiveData<Boolean>()
    val unspecifyDateFrom: LiveData<Boolean>
        get() = _unspecifyDateFrom

    val _dateFromString = MutableLiveData<String>()
    val dateFromString: LiveData<String>
        get() = _dateFromString

    val _unspecifyTimeFrom = MutableLiveData<Boolean>()
    val unspecifyTimeFrom: LiveData<Boolean>
        get() = _unspecifyTimeFrom

    val _timeFromString = MutableLiveData<String>()
    val timeFromString: LiveData<String>
        get() = _timeFromString

    val _unspecifyDateTo = MutableLiveData<Boolean>()
    val unspecifyDateTo: LiveData<Boolean>
        get() = _unspecifyDateTo

    val _dateToString = MutableLiveData<String>()
    val dateToString: LiveData<String>
        get() = _dateToString

    val _unspecifyTimeTo = MutableLiveData<Boolean>()
    val unspecifyTimeTo: LiveData<Boolean>
        get() = _unspecifyTimeTo

    val _timeToString = MutableLiveData<String>()
    val timeToString: LiveData<String>
        get() = _timeToString

    val _visibility = MutableLiveData<Boolean>()
    val visibility: LiveData<Boolean>
        get() = _visibility


    val dateAndTimeFrom = Calendar.getInstance()
    val dateAndTimeTo = Calendar.getInstance()

    private var taskId: Int? = null

    val serverTaskManager = ServerTaskManager(this)

    private var sharedPrefs = SharedPreferencesOperations(application)

    constructor(task: TaskComparable, application: Application, onSaveTaskCallback: ()->Unit) : this(application, onSaveTaskCallback) {
        _name.value = task.GetName()
        _comment.value = task.GetComment()
        _unspecifyDateFrom.value = false
        _unspecifyTimeFrom.value = false
        _unspecifyDateTo.value = false
        _unspecifyTimeTo.value = false

        val dateFrom = task.GetDateFrom().toString()
        if (!dateFrom.isNullOrEmpty())
            SetDateFromCommand(DateAndTimeConverter.GetYearFromISOStringDate(dateFrom), DateAndTimeConverter.GetMonthFromISOStringDate(dateFrom) - 1, DateAndTimeConverter.GetDayFromISOStringDate(dateFrom))
        else
            _unspecifyDateFrom.value = true

        val timeFrom = task.GetTimeFrom().toString()
        if (!timeFrom.isNullOrEmpty())
            SetTimeFromCommand(DateAndTimeConverter.GetHourFromISOStringTime(timeFrom), DateAndTimeConverter.GetMinutesFromISOStringTime(timeFrom))
        else
            _unspecifyTimeFrom.value = true
        val dateTo = task.GetDateTo().toString()
        if (!dateTo.isNullOrEmpty())
            SetDateToCommand(DateAndTimeConverter.GetYearFromISOStringDate(dateTo), DateAndTimeConverter.GetMonthFromISOStringDate(dateTo) - 1, DateAndTimeConverter.GetDayFromISOStringDate(dateTo))
        else
            _unspecifyDateTo.value = true
        val timeTo = task.GetTimeTo().toString()
        if (!timeTo.isNullOrEmpty())
            SetTimeToCommand(DateAndTimeConverter.GetHourFromISOStringTime(timeTo), DateAndTimeConverter.GetMinutesFromISOStringTime(timeTo))
        else
            _unspecifyTimeTo.value = true

        _visibility.value = task.GetVisibility()

        taskId = task.GetTaskNumber()

    }

    init {
        _name.value = ""
        _comment.value = ""
        _unspecifyDateFrom.value = true
        _unspecifyTimeFrom.value = true
        _unspecifyDateTo.value = true
        _unspecifyTimeTo.value = true

        SetDateFromCommand(dateAndTimeFrom[Calendar.YEAR], dateAndTimeFrom[Calendar.MONTH], dateAndTimeFrom[Calendar.DAY_OF_MONTH])
        SetTimeFromCommand(dateAndTimeFrom[Calendar.HOUR_OF_DAY], dateAndTimeFrom[Calendar.MINUTE])
        SetDateToCommand(dateAndTimeTo[Calendar.YEAR], dateAndTimeTo[Calendar.MONTH], dateAndTimeTo[Calendar.DAY_OF_MONTH])
        SetTimeToCommand(dateAndTimeTo[Calendar.HOUR_OF_DAY], dateAndTimeTo[Calendar.MINUTE])

        _visibility.value = true
    }


    fun SetDateFromCommand(year: Int, month: Int, date: Int) {
        val dispMonth = month + 1
        dateAndTimeFrom[year, month] = date
        _dateFromString.value = DateAndTimeConverter.ConvertToStringDate(year, dispMonth, date)
    }

    fun SetTimeFromCommand(hours: Int, minutes: Int) {
        dateAndTimeFrom[Calendar.HOUR_OF_DAY] = hours
        dateAndTimeFrom[Calendar.MINUTE] = minutes
        _timeFromString.value = DateAndTimeConverter.ConvertToStringTime(hours, minutes)
    }

    fun SetDateToCommand(year: Int, month: Int, date: Int) {
        val dispMonth = month + 1
        dateAndTimeTo[year, month] = date
        _dateToString.value = DateAndTimeConverter.ConvertToStringDate(year, dispMonth, date)
    }

    fun SetTimeToCommand(hours: Int, minutes: Int) {
        dateAndTimeTo[Calendar.HOUR_OF_DAY] = hours
        dateAndTimeTo[Calendar.MINUTE] = minutes
        _timeToString.value = DateAndTimeConverter.ConvertToStringTime(hours, minutes)
    }

    fun SaveTask() {
        val task = GetTask()
        when (taskId) {
            null -> serverTaskManager.AddTask(sharedPrefs.login.toString(), task)
            else -> serverTaskManager.UpdateTask(sharedPrefs.login.toString(), task, taskId!!)
        }
        onSaveTaskCallback()
    }

    private fun GetTask(): ServerTask {
        val task = ServerTask(name.value)
        task.SetComment(comment.value)
        visibility.value?.let { task.SetVisibility(it) }

        if (!unspecifyDateFrom.value!!) {
            val strDateFrom = dateFromString.value
            val year = DateAndTimeConverter.GetYearFromStringDate(strDateFrom.toString())
            val month = DateAndTimeConverter.GetMonthFromStringDate(strDateFrom.toString())
            val date = DateAndTimeConverter.GetDayFromStringDate(strDateFrom.toString())
            task.SetDateFrom(year, month, date)
        }

        if (!unspecifyTimeFrom.value!!) {
            val strTimeFrom = timeFromString.value
            val hour = DateAndTimeConverter.GetHourFromStringTime(strTimeFrom.toString())
            val minutes = DateAndTimeConverter.GetMinutesFromStringTime(strTimeFrom.toString())
            task.SetTimeFrom(hour, minutes)
        }

        if (!unspecifyDateTo.value!!) {
            val strDateTo: String? = dateToString.value
            val year = DateAndTimeConverter.GetYearFromStringDate(strDateTo.toString())
            val month = DateAndTimeConverter.GetMonthFromStringDate(strDateTo.toString())
            val date = DateAndTimeConverter.GetDayFromStringDate(strDateTo.toString())
            task.SetDateTo(year, month, date)
        }

        if (!unspecifyTimeTo.value!!) {
            val strTimeTo = timeToString.value
            val hour = DateAndTimeConverter.GetHourFromStringTime(strTimeTo.toString())
            val minutes = DateAndTimeConverter.GetMinutesFromStringTime(strTimeTo.toString())
            task.SetTimeTo(hour, minutes)
        }

        return task
    }


    override fun OnGetTasks(tasksFromServer: ArrayList<ServerTask>) {}

    override fun OnDeleteTasks() { }

}