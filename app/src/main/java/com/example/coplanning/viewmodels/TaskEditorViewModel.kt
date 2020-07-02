package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.task.ITaskListOperations
import com.example.coplanning.models.task.ServerTask
import com.example.coplanning.models.task.ServerTaskManager
import com.example.coplanning.models.task.TaskComparable
import java.util.*

class TaskEditorViewModel(val application: Application, val onSaveTaskCallback: ()->Unit
) : ViewModel(),
    ITaskListOperations {

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

    val dateAndTimeFrom: Calendar = Calendar.getInstance()
    val dateAndTimeTo: Calendar = Calendar.getInstance()
    private var taskId: Int? = null
    private val serverTaskManager = ServerTaskManager(this)
    private var sharedPrefs = SharedPreferencesOperations(application)

    constructor(task: TaskComparable, application: Application, onSaveTaskCallback: ()->Unit)
            : this(application, onSaveTaskCallback) {
        _name.value = task.getName()
        _comment.value = task.getComment()
        _unspecifyDateFrom.value = false
        _unspecifyTimeFrom.value = false
        _unspecifyDateTo.value = false
        _unspecifyTimeTo.value = false

        val dateFrom = task.getDateFrom().toString()
        if (!dateFrom.isNullOrEmpty()) {
            setDateFromCommand(
                DateAndTimeConverter.getYearFromISOStringDate(dateFrom),
                DateAndTimeConverter.getMonthFromISOStringDate(dateFrom) - 1,
                DateAndTimeConverter.getDayFromISOStringDate(dateFrom)
            )
        } else {
            _unspecifyDateFrom.value = true
        }

        val timeFrom = task.getTimeFrom().toString()
        if (!timeFrom.isNullOrEmpty()) {
            setTimeFromCommand(
                DateAndTimeConverter.getHourFromISOStringTime(timeFrom),
                DateAndTimeConverter.getMinutesFromISOStringTime(timeFrom)
            )
        } else {
            _unspecifyTimeFrom.value = true
        }
        val dateTo = task.getDateTo().toString()
        if (!dateTo.isNullOrEmpty()) {
            setDateToCommand(
                DateAndTimeConverter.getYearFromISOStringDate(dateTo),
                DateAndTimeConverter.getMonthFromISOStringDate(dateTo) - 1,
                DateAndTimeConverter.getDayFromISOStringDate(dateTo)
            )
        } else {
            _unspecifyDateTo.value = true
        }
        val timeTo = task.getTimeTo().toString()
        if (!timeTo.isNullOrEmpty()) {
            setTimeToCommand(
                DateAndTimeConverter.getHourFromISOStringTime(timeTo),
                DateAndTimeConverter.getMinutesFromISOStringTime(timeTo)
            )
        } else {
            _unspecifyTimeTo.value = true
        }
        _visibility.value = task.getVisibility()
        taskId = task.getTaskNumber()
    }

    init {
        _name.value = ""
        _comment.value = ""
        _unspecifyDateFrom.value = true
        _unspecifyTimeFrom.value = true
        _unspecifyDateTo.value = true
        _unspecifyTimeTo.value = true

        setDateFromCommand(dateAndTimeFrom[Calendar.YEAR], dateAndTimeFrom[Calendar.MONTH], dateAndTimeFrom[Calendar.DAY_OF_MONTH])
        setTimeFromCommand(dateAndTimeFrom[Calendar.HOUR_OF_DAY], dateAndTimeFrom[Calendar.MINUTE])
        setDateToCommand(dateAndTimeTo[Calendar.YEAR], dateAndTimeTo[Calendar.MONTH], dateAndTimeTo[Calendar.DAY_OF_MONTH])
        setTimeToCommand(dateAndTimeTo[Calendar.HOUR_OF_DAY], dateAndTimeTo[Calendar.MINUTE])

        _visibility.value = true
    }


    fun setDateFromCommand(year: Int, month: Int, date: Int) {
        val dispMonth = month + 1
        dateAndTimeFrom[year, month] = date
        _dateFromString.value = DateAndTimeConverter.convertToStringDate(year, dispMonth, date)
    }

    fun setTimeFromCommand(hours: Int, minutes: Int) {
        dateAndTimeFrom[Calendar.HOUR_OF_DAY] = hours
        dateAndTimeFrom[Calendar.MINUTE] = minutes
        _timeFromString.value = DateAndTimeConverter.convertToStringTime(hours, minutes)
    }

    fun setDateToCommand(year: Int, month: Int, date: Int) {
        val dispMonth = month + 1
        dateAndTimeTo[year, month] = date
        _dateToString.value = DateAndTimeConverter.convertToStringDate(year, dispMonth, date)
    }

    fun setTimeToCommand(hours: Int, minutes: Int) {
        dateAndTimeTo[Calendar.HOUR_OF_DAY] = hours
        dateAndTimeTo[Calendar.MINUTE] = minutes
        _timeToString.value = DateAndTimeConverter.convertToStringTime(hours, minutes)
    }

    fun saveTask() {
        val task = getTask()
        when (taskId) {
            null -> serverTaskManager.addTask(sharedPrefs.login.toString(), task)
            else -> serverTaskManager.updateTask(sharedPrefs.login.toString(), task, taskId!!)
        }
        onSaveTaskCallback()
    }

    private fun getTask(): ServerTask {
        val task = ServerTask(name.value)
        task.setComment(comment.value)
        visibility.value?.let { task.setVisibility(it) }

        if (!unspecifyDateFrom.value!!) {
            val strDateFrom = dateFromString.value
            val year = DateAndTimeConverter.getYearFromStringDate(strDateFrom.toString())
            val month = DateAndTimeConverter.getMonthFromStringDate(strDateFrom.toString())
            val date = DateAndTimeConverter.getDayFromStringDate(strDateFrom.toString())
            task.setDateFrom(year, month, date)
        }

        if (!unspecifyTimeFrom.value!!) {
            val strTimeFrom = timeFromString.value
            val hour = DateAndTimeConverter.getHourFromStringTime(strTimeFrom.toString())
            val minutes = DateAndTimeConverter.getMinutesFromStringTime(strTimeFrom.toString())
            task.setTimeFrom(hour, minutes)
        }

        if (!unspecifyDateTo.value!!) {
            val strDateTo: String? = dateToString.value
            val year = DateAndTimeConverter.getYearFromStringDate(strDateTo.toString())
            val month = DateAndTimeConverter.getMonthFromStringDate(strDateTo.toString())
            val date = DateAndTimeConverter.getDayFromStringDate(strDateTo.toString())
            task.setDateTo(year, month, date)
        }

        if (!unspecifyTimeTo.value!!) {
            val strTimeTo = timeToString.value
            val hour = DateAndTimeConverter.getHourFromStringTime(strTimeTo.toString())
            val minutes = DateAndTimeConverter.getMinutesFromStringTime(strTimeTo.toString())
            task.setTimeTo(hour, minutes)
        }

        return task
    }

    override fun onGetTasks(tasksFromServer: ArrayList<ServerTask>) {}

    override fun onDeleteTasks() { }

}