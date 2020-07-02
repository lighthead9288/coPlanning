package com.example.coplanning.models.task

import com.example.coplanning.models.task.ServerTask

class UserTask(val task: ServerTask, val username: String): ServerTask(task)