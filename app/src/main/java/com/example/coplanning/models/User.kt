package com.example.coplanning.models

import java.io.Serializable
import java.util.*

data class User(var name: String, var surname: String, var username: String, var password: String, var taskList: List<ServerTask>, var subscriberList: List<String>): Serializable
/*{
    private val _v = 0
    private val _id = String()
}*/