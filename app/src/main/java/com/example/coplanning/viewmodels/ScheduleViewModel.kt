package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.task.*
import com.github.nkzawa.emitter.Emitter

import org.json.JSONObject
import java.util.*

class ScheduleViewModel(val application: Application, val username: String
) : ViewModel(),
    ITaskListOperations {

    private val sharedPrefs = SharedPreferencesOperations(application)

    val _dateAndTimeFromString = MutableLiveData<String>()
    val dateAndTimeFromString: LiveData<String>
        get() = _dateAndTimeFromString

    val _dateAndTimeToString = MutableLiveData<String>()
    val dateAndTimeToString: LiveData<String>
        get() = _dateAndTimeToString

    val _isToday = MutableLiveData<Boolean>()
    val isToday: LiveData<Boolean>
        get() = _isToday

    val _isThisWeek = MutableLiveData<Boolean>()
    val isThisWeek: LiveData<Boolean>
        get() = _isThisWeek

    val _isInterval = MutableLiveData<Boolean>()
    val isInterval: LiveData<Boolean>
        get() = _isInterval

    val _isCalendar = MutableLiveData<Boolean>()
    val isCalendar: LiveData<Boolean>
        get() = _isCalendar

    val _isParams = MutableLiveData<Boolean>()
    val isParams: LiveData<Boolean>
        get() = _isParams

    val _groupedTaskList = MutableLiveData<GroupedTasksCollection>()
    val groupedTaskList: LiveData<GroupedTasksCollection>
        get() = _groupedTaskList

    val _isMe = MutableLiveData<Boolean>()
    val isMe: LiveData<Boolean>
        get() = _isMe

    val dateAndTimeFrom: Calendar = Calendar.getInstance()
    val dateAndTimeTo: Calendar = Calendar.getInstance()

    private val serverTaskManager: ServerTaskManager = ServerTaskManager(this)
    private val socketClient: SocketClient = SocketClient()

    init {
        _isMe.value = isMe()
        setIntervalCommand()
        hideCalendarAndParams()
    }

    private fun isMe(): Boolean {
        return getCurUser()==username
    }

    fun getCurUser():String {
        return sharedPrefs.login.toString()
    }

    private fun hideCalendarAndParams() {
        _isCalendar.value = false
        _isParams.value = false
    }

    fun setCalendarCommand() {
        _isCalendar.value = !_isCalendar.value!!
        _isParams.value = false
    }

    fun setParamsCommand() {
        _isParams.value = !_isParams.value!!
        _isCalendar.value = false
    }

    fun setTodayCommand() {
        _isToday.value = true

        val from: Calendar = GregorianCalendar()
        from[Calendar.HOUR_OF_DAY] = 0
        from[Calendar.MINUTE] = 0
        from[Calendar.SECOND] = 0

        val to = GregorianCalendar()
        to[Calendar.HOUR_OF_DAY] = 23
        to[Calendar.MINUTE] = 59
        to[Calendar.SECOND] = 59

        setIntervalCommand(from, to)
    }

    fun setThisWeekCommand() {
        _isThisWeek.value = true

        val from: Calendar = GregorianCalendar()
        from.add(Calendar.DAY_OF_WEEK, -from[Calendar.DAY_OF_WEEK] + 1)

        val to = GregorianCalendar()
        to.add(Calendar.DAY_OF_WEEK, 7 - to[Calendar.DAY_OF_WEEK] + 1)

        setIntervalCommand(from, to)
    }

    fun setIntervalCommand() {
        _isInterval.value = true
        setIntervalCommand(dateAndTimeFrom, dateAndTimeTo)
        getTasks()
    }

    fun setIntervalCommand(from: Calendar, to: Calendar) {
        setFromDateCommand(from)
        setToDateCommand(to)
        getTasks()
    }


    fun setFromDateCommand(fullDate: Calendar) {
        val date = fullDate[Calendar.DAY_OF_MONTH]
        val month = fullDate[Calendar.MONTH]
        val year = fullDate[Calendar.YEAR]
        val dispMonth = month + 1
        dateAndTimeFrom[year, month] = date
        _dateAndTimeFromString.value = DateAndTimeConverter.convertToStringDate(year, dispMonth, date)

        setMinTime(dateAndTimeFrom)

    }

    fun setToDateCommand(fullDate: Calendar) {
        val date = fullDate[Calendar.DAY_OF_MONTH]
        val month = fullDate[Calendar.MONTH]
        val year = fullDate[Calendar.YEAR]
        val dispMonth = month + 1
        dateAndTimeTo[year, month] = date
        _dateAndTimeToString.value = DateAndTimeConverter.convertToStringDate(year, dispMonth, date)

        getMaxTime(dateAndTimeTo)

    }

    fun getTasks() {
        serverTaskManager.getTasksFromServer(username, dateAndTimeFrom, dateAndTimeTo, "")

    }

    fun getTasks(filter: String) {
        serverTaskManager.getTasksFromServer(username, dateAndTimeFrom, dateAndTimeTo, filter)

    }

    fun getTasks(dFrom: Calendar, dTo: Calendar, filter: String) {
        serverTaskManager.getTasksFromServer(username, dFrom, dTo, filter)
    }

    fun deleteTask(task: TaskComparable) {
        serverTaskManager.deleteTask(username, task.getTaskNumber())
    }

    fun setTaskCompleted(task: TaskComparable) {
        serverTaskManager.updateTask(username, task, task.getTaskNumber())
    }

    private var taskSubscribeViewChangeCallback: ((subscriberList: List<String>)->Unit)? = null

    fun subscribeOnUserTask(task: TaskComparable, direction: Boolean, taskSubscribeViewChangeCallback: (subscriberList: List<String>)->Unit) {
        this.taskSubscribeViewChangeCallback = taskSubscribeViewChangeCallback
        socketClient.setUserTaskSubscribeListener(onTaskSubscribeAnswer)
        socketClient.subscribeOnUserTask(username, getCurUser(), direction, task.getTaskNumber())
    }

    private val onTaskSubscribeAnswer = Emitter.Listener {  args ->
        val jsonTask = args[3] as JSONObject
        val list = jsonTask.getJSONArray("subscriberList")

        val subscribers = Array(list.length()) {
            list.getString(it)
        }

        taskSubscribeViewChangeCallback?.let {
            it(subscribers.toList())
        }
    }

    private fun setMinTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
    }

    private fun getMaxTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
    }

    override fun onGetTasks(tasksFromServer: ArrayList<ServerTask>) {
        val groupsArrayList = ArrayList<String>()
        val resultTaskList = ArrayList<DayWithTasks>()

        val taskComparables = ArrayList<TaskComparable>()
        for (task in tasksFromServer) {
            val taskComparable =
                TaskComparable(task)
            taskComparables.add(taskComparable)
        }
        Collections.sort(taskComparables, TaskComparable.dateTimeFromComparator)

        for (task in taskComparables) {
            val dateFrom = task.getDateFrom()
            val curTaskDateTimeFrom = task.getDateTimeFrom()
            val dayOfWeek = curTaskDateTimeFrom[Calendar.DAY_OF_WEEK]
            val strDayOfWeek = getStrWeekDay(dayOfWeek)
            val fullDayView = "$strDayOfWeek, $dateFrom"
            if (!groupsArrayList.contains(fullDayView)) {
                groupsArrayList.add(fullDayView)
                val curDateTasks: ArrayList<TaskComparable> =
                    getCurDateTasks(taskComparables, dateFrom.toString())
                resultTaskList.add((DayWithTasks(
                    fullDayView,
                    curDateTasks
                )))
            }
        }
        _groupedTaskList.value = GroupedTasksCollection(resultTaskList)
        hideCalendarAndParams()

    }

    override fun onDeleteTasks() {
        getTasks()
    }


    private fun getCurDateTasks(tasks: ArrayList<TaskComparable>, dateFrom: String
    ): ArrayList<TaskComparable> {
        val resultList = ArrayList<TaskComparable>()
        for (task in tasks) {
            val curDate = task.getDateFrom()
            if (curDate == dateFrom) {
                resultList.add(task)
            }
        }
        return resultList
    }

    private fun getStrWeekDay(dayNumber: Int): String? {
        return when (dayNumber) {
            6 -> "SUN"
            7 -> "MON"
            1 -> "TUE"
            2 -> "WED"
            3 -> "THU"
            4 -> "FRI"
            5 -> "SAT"
            else -> ""
        }
    }


}