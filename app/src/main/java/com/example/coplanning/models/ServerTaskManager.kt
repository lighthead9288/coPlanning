package com.example.coplanning.models

import com.example.coplanning.communication.ICoPlanningAPI
import com.example.coplanning.communication.RetrofitClient
import com.example.coplanning.helpers.DateAndTimeConverter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class ServerTaskManager(ops: ITaskListOperations?) {
    private val iTaskListOperations: ITaskListOperations? = ops
    private var getUserTaskListCall: Call<User>? = null
    private var addTaskCall: Call<String>? = null
    private var deleteTaskCall: Call<String>? = null
    private var editTaskCall: Call<String>? = null
    private var client: ICoPlanningAPI? = null
    private fun ClientInit() {
        val rClient = RetrofitClient()
        val retrofit: Retrofit = rClient.GetRetrofitEntity()
        client = retrofit.create<ICoPlanningAPI>(ICoPlanningAPI::class.java)
    }

    fun GetTasksFromServer(
        user: String?,
        dateTimeFrom: Calendar,
        dateTimeTo: Calendar,
        taskFilter: String?
    ) {
        val dateFrom: String? = DateAndTimeConverter.GetISOStringDateFromCalendar(dateTimeFrom)
        val timeFrom: String? = DateAndTimeConverter.GetISOStringTimeFromCalendar(dateTimeFrom)
        val dateTo: String? = DateAndTimeConverter.GetISOStringDateFromCalendar(dateTimeTo)
        val timeTo: String? = DateAndTimeConverter.GetISOStringTimeFromCalendar(dateTimeTo)
        getUserTaskListCall =
            client?.getUserTaskList(user, dateFrom, timeFrom, dateTo, timeTo, taskFilter)
        getUserTaskListCall!!.enqueue(object : Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                val user = response.body()
                val tasksFromServer: ArrayList<ServerTask> = user.taskList as ArrayList<ServerTask>
                iTaskListOperations?.OnGetTasks(tasksFromServer)
            }

            override fun onFailure(
                call: Call<User>,
                t: Throwable
            ) {
            }
        })
    }

    fun AddTask(username: String, task: ServerTask) {
        val curUserTask = UserTask(task, username)
        addTaskCall = client?.addTask(curUserTask)
        addTaskCall!!.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
            }

            override fun onFailure(
                call: Call<String?>,
                t: Throwable
            ) {
            }
        })
    }

    fun DeleteTask(
        username: String?,
        taskId: Int
    ) {
        deleteTaskCall = client?.deleteTask(username, taskId)
        deleteTaskCall!!.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                iTaskListOperations?.OnDeleteTasks()
            }

            override fun onFailure(
                call: Call<String?>,
                t: Throwable
            ) {
                iTaskListOperations?.OnDeleteTasks()
            }
        })
    }

    fun UpdateTask(username: String, task: ServerTask, taskId: Int) {
        val curUserTask = UserTask(task, username)
        curUserTask.taskNumber = taskId

       // curUserTask.SetTaskId(taskId)
        editTaskCall = client?.editTask(curUserTask)
        editTaskCall!!.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
            }

            override fun onFailure(
                call: Call<String?>,
                t: Throwable
            ) {
            }
        })
    }

    init {
        ClientInit()
    }
}

interface ITaskListOperations {
    fun OnGetTasks(tasksFromServer: ArrayList<ServerTask>)
    fun OnDeleteTasks()
}