package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.*
import com.github.nkzawa.emitter.Emitter
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.json.JSONObject
import java.util.*

class ScheduleViewModel(val application: Application, val username: String): ViewModel(), ITaskListOperations {

    private val sharedPrefs = SharedPreferencesOperations(application)

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    val dateAndTimeFrom = Calendar.getInstance()
    val dateAndTimeTo = Calendar.getInstance()


    private val serverTaskManager: ServerTaskManager = ServerTaskManager(this)

    private val socketClient: SocketClient = SocketClient()



    init {
        _isMe.value = IsMe()
        SetIntervalCommand()
        HideCalendarAndParams()
    }

    fun IsMe(): Boolean {
        return GetCurUser()==username
    }

    fun GetCurUser():String {
        return sharedPrefs.login.toString()
    }

    fun HideCalendarAndParams() {
        _isCalendar.value = false
        _isParams.value = false
    }

    fun SetCalendarCommand() {

        _isCalendar.value = !_isCalendar.value!!
        _isParams.value = false
    }

    fun SetParamsCommand() {
        _isParams.value = !_isParams.value!!
        _isCalendar.value = false
    }

    fun SetTodayCommand() {
        _isToday.value = true

        val from: Calendar = GregorianCalendar()
        from[Calendar.HOUR_OF_DAY] = 0
        from[Calendar.MINUTE] = 0
        from[Calendar.SECOND] = 0

        val to = GregorianCalendar()
        to[Calendar.HOUR_OF_DAY] = 23
        to[Calendar.MINUTE] = 59
        to[Calendar.SECOND] = 59

        SetIntervalCommand(from, to)
    }

    fun SetThisWeekCommand() {
        _isThisWeek.value = true

        var from: Calendar = GregorianCalendar()
        from.add(Calendar.DAY_OF_WEEK, -from[Calendar.DAY_OF_WEEK] + 1)

        var to = GregorianCalendar()
        to.add(Calendar.DAY_OF_WEEK, 7 - to[Calendar.DAY_OF_WEEK] + 1)

        SetIntervalCommand(from, to)
    }

    fun SetIntervalCommand() {
        _isInterval.value = true
        SetIntervalCommand(dateAndTimeFrom, dateAndTimeTo)
        GetTasks()
    }

    fun SetIntervalCommand(from: Calendar, to: Calendar) {
        SetFromDateCommand(from)
        SetToDateCommand(to)
        GetTasks()
    }


    fun SetFromDateCommand(fullDate: Calendar) {
        val date = fullDate[Calendar.DAY_OF_MONTH]
        val month = fullDate[Calendar.MONTH]
        val year = fullDate[Calendar.YEAR]
        val dispMonth = month + 1
        dateAndTimeFrom[year, month] = date
        _dateAndTimeFromString.value = DateAndTimeConverter.ConvertToStringDate(year, dispMonth, date)

        //savedStateHandle.set(DATE_AND_TIME_FROM, fullDate)

        SetMinTime(dateAndTimeFrom)

    }

    fun SetToDateCommand(fullDate: Calendar) {
        val date = fullDate[Calendar.DAY_OF_MONTH]
        val month = fullDate[Calendar.MONTH]
        val year = fullDate[Calendar.YEAR]
        val dispMonth = month + 1
        dateAndTimeTo[year, month] = date
        _dateAndTimeToString.value = DateAndTimeConverter.ConvertToStringDate(year, dispMonth, date)

        SetMaxTime(dateAndTimeTo)


        //  savedStateHandle.set(DATE_AND_TIME_TO, fullDate)
    }

    fun GetTasks() {
        serverTaskManager.GetTasksFromServer(username, dateAndTimeFrom, dateAndTimeTo, "")

    }

    fun GetTasks(filter: String) {
        serverTaskManager.GetTasksFromServer(username, dateAndTimeFrom, dateAndTimeTo, filter)

    }

    fun GetTasks(dFrom: Calendar, dTo: Calendar, filter: String) {
        serverTaskManager.GetTasksFromServer(username, dFrom, dTo, filter)
    }

    fun DeleteTask(task: TaskComparable) {
        serverTaskManager.DeleteTask(username, task.GetTaskNumber())
    }

    fun SetTaskCompleted(task: TaskComparable) {
        serverTaskManager.UpdateTask(username, task, task.GetTaskNumber())
    }

    private var taskSubscribeViewChangeCallback: ((subscriberList: List<String>)->Unit)? = null

    fun SubscribeOnUserTask(task: TaskComparable, direction: Boolean, taskSubscribeViewChangeCallback: (subscriberList: List<String>)->Unit) {
        this.taskSubscribeViewChangeCallback = taskSubscribeViewChangeCallback
        socketClient.SetUserTaskSubscribeListener(onTaskSubscribeAnswer)
        socketClient.SubscribeOnUserTask(username, GetCurUser(), direction, task.GetTaskNumber())
    }

    private val onTaskSubscribeAnswer = Emitter.Listener {  args ->
        val jsonTask = args[3] as JSONObject
        val list = jsonTask.getJSONArray("subscriberList")

        var subscribers = Array(list.length()) {
            list.getString(it)
        }

        taskSubscribeViewChangeCallback?.let {
           // it(task)
            it(subscribers.toList())
        }
       // GetTasks()
    }

    private fun SetMinTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
    }

    private fun SetMaxTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
    }

    override fun OnGetTasks(tasksFromServer: ArrayList<ServerTask>) {
        val groupsArrayList = ArrayList<String>()
        val resultTaskList = ArrayList<DayWithTasks>()

        val taskComparables = ArrayList<TaskComparable>()
        for (task in tasksFromServer) {
            val taskComparable = TaskComparable(task)
            taskComparables.add(taskComparable)
        }
        Collections.sort(taskComparables, TaskComparable.DateTimeFromComparator)

        for (task in taskComparables) {
            val dateFrom = task.GetDateFrom()
            val curTaskDateTimeFrom = task.GetDateTimeFrom()
            val dayOfWeek = curTaskDateTimeFrom[Calendar.DAY_OF_WEEK]
            val strDayOfWeek = GetStrWeekDay(dayOfWeek)
            val fullDayView = "$strDayOfWeek, $dateFrom"
            if (!groupsArrayList.contains(fullDayView)) {
                groupsArrayList.add(fullDayView)
                val curDateTasks: ArrayList<TaskComparable> =
                    GetCurDateTasks(taskComparables, dateFrom.toString())
                resultTaskList.add((DayWithTasks(fullDayView, curDateTasks)))
            }
        }

        _groupedTaskList.value = GroupedTasksCollection(resultTaskList)
        HideCalendarAndParams()

    }

    override fun OnDeleteTasks() {
        GetTasks()
    }


    private fun GetCurDateTasks(tasks: ArrayList<TaskComparable>, dateFrom: String): ArrayList<TaskComparable> {
        val resultList = ArrayList<TaskComparable>()
        for (task in tasks) {
            val curDate = task.GetDateFrom()
            if (curDate == dateFrom) {
                resultList.add(task)
            }
        }
        return resultList
    }

    fun GetStrWeekDay(dayNumber: Int): String? {
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