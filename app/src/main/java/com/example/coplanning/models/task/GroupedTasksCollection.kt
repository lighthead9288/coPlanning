package com.example.coplanning.models.task

data class GroupedTasksCollection(val tasksDays: List<DayWithTasks>)

data class DayWithTasks(val day: String, val tasks: List<TaskComparable>)