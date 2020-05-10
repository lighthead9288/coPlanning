package com.example.coplanning.models

class UserTask(val task: ServerTask, val username: String): ServerTask(task) {

}