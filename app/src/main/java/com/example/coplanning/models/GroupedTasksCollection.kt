package com.example.coplanning.models

import java.util.*

data class GroupedTasksCollection(val tasksDays: List<DayWithTasks>)

data class DayWithTasks(val day: String, val tasks: List<TaskComparable>)