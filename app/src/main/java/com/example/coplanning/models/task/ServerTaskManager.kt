package com.example.coplanning.models.task

import com.example.coplanning.communication.ICoPlanningAPI
import com.example.coplanning.communication.RetrofitClient
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.user.User
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

    init {
        clientInit()
    }

    private fun clientInit() {
        val rClient = RetrofitClient()
        val retrofit: Retrofit = rClient.getRetrofitEntity()
        client = retrofit.create<ICoPlanningAPI>(ICoPlanningAPI::class.java)
    }

    fun getTasksFromServer(
        user: String?,
        dateTimeFrom: Calendar,
        dateTimeTo: Calendar,
        taskFilter: String?
    ) {
        val dateFrom: String? = DateAndTimeConverter.getISOStringDateFromCalendar(dateTimeFrom)
        val timeFrom: String? = DateAndTimeConverter.getISOStringTimeFromCalendar(dateTimeFrom)
        val dateTo: String? = DateAndTimeConverter.getISOStringDateFromCalendar(dateTimeTo)
        val timeTo: String? = DateAndTimeConverter.getISOStringTimeFromCalendar(dateTimeTo)
        getUserTaskListCall =
            client?.getUserTaskList(user, dateFrom, timeFrom, dateTo, timeTo, taskFilter)
        getUserTaskListCall!!.enqueue(object : Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                val user = response.body()
                val tasksFromServer: ArrayList<ServerTask> = user?.taskList as ArrayList<ServerTask>
                iTaskListOperations?.onGetTasks(tasksFromServer)
            }

            override fun onFailure(
                call: Call<User>,
                t: Throwable
            ) {
            }
        })
    }

    fun addTask(username: String, task: ServerTask) {
        val curUserTask =
            UserTask(task, username)
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

    fun deleteTask(
        username: String?,
        taskId: Int
    ) {
        deleteTaskCall = client?.deleteTask(username, taskId)
        deleteTaskCall!!.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                iTaskListOperations?.onDeleteTasks()
            }

            override fun onFailure(
                call: Call<String?>,
                t: Throwable
            ) {
                iTaskListOperations?.onDeleteTasks()
            }
        })
    }

    fun updateTask(username: String, task: ServerTask, taskId: Int) {
        val curUserTask =
            UserTask(task, username)
        curUserTask.setTaskNumber(taskId)

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


}

interface ITaskListOperations {
    fun onGetTasks(tasksFromServer: ArrayList<ServerTask>)
    fun onDeleteTasks()
}