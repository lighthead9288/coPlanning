package com.example.coplanning.models.user

import com.example.coplanning.models.task.ServerTask
import java.io.Serializable

data class User(val name: String, val surname: String, val username: String, val password: String, val taskList: List<ServerTask>, var subscriberList: List<String>): Serializable
