package com.example.coplanning.communication

import com.example.coplanning.models.User
import com.example.coplanning.models.UserTask
import retrofit2.Call
import retrofit2.http.*

interface ICoPlanningAPI {

    @GET("/api/getAllUsers")
    fun usersList(): Call<List<User?>?>?

    @GET("/api/getUserById/{id}")
    fun getUser(@Path("id") id: String?): Call<User?>?

    @POST("/api/extLogin")
    fun login(
        @Query("username") username: String?,
        @Query("password") password: String?
    ): Call<User>

    @POST("/api/extRegister")
    fun register(@Body user: User): Call<User>

    @POST("/api/extSetUserTaskList")
    fun setUserTaskList(@Body user: User?): Call<User?>?

    @GET("/api/extGetUserTaskList")
    fun getUserTaskList(
        @Query("username") username: String?,
        @Query("dateFrom") dateFrom: String?,
        @Query("timeFrom") timeFrom: String?,
        @Query("dateTo") dateTo: String?,
        @Query("timeTo") timeTo: String?,
        @Query("taskFilter") taskFilter: String?
    ): Call<User>

    //@GET("/api/extGetUserUnavailableTime")


    //@GET("/api/extGetUserUnavailableTime")
    @POST("/api/extAddTask")
    fun addTask(@Body params: UserTask?): Call<String>

    @POST("/api/extDeleteTask")
    fun deleteTask(
        @Query("username") username: String?,
        @Query("taskId") taskId: Int
    ): Call<String>

    @POST("/api/extEditTask")
    fun editTask(@Body params: UserTask?): Call<String>
}