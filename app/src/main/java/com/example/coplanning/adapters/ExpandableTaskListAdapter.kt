package com.example.coplanning.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.coplanning.R
import com.example.coplanning.models.task.DayWithTasks
import com.example.coplanning.models.task.TaskComparable


class ExpandableTaskListAdapter internal constructor(
    private val context: Context,
    private val groupsList: List<String>,
    private val taskCollection: List<DayWithTasks>,
    private val groupLayout: Int,
    private val taskLayout: Int,
    private val username: String,
    private val taskSetCompletedCallback: (task: TaskComparable)->Unit,
    private val taskDeleteCallback: (task: TaskComparable)->Unit,
    private val taskSubscribeCallback: (
        task: TaskComparable,
        direction: Boolean,
        callback: (subscriberList: List<String>)->Unit)->Unit)
    : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return groupsList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return taskCollection[groupPosition].tasks.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return groupsList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return taskCollection[groupPosition].tasks[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        val groupTitle = getGroup(groupPosition) as String
        val expListView = parent as ExpandableListView
        expListView.expandGroup(groupPosition)
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(groupLayout, null)
        }
        val groupTitleTv = convertView?.findViewById<TextView>(R.id.taskGroupName)
        groupTitleTv?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        groupTitleTv?.text = groupTitle
        return convertView
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        val task: TaskComparable = getChild(groupPosition, childPosition) as TaskComparable
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(taskLayout, null)
        }
        val tNameView = convertView?.findViewById<TextView>(R.id.taskName)
        val tCommentView = convertView?.findViewById<TextView>(R.id.taskComment)
        val tDateTimeInterval =
            convertView?.findViewById<TextView>(R.id.taskDateTimeInterval)
        val tCompletedView = convertView?.findViewById<CheckBox>(R.id.taskCompleted)
        tCompletedView?.setOnClickListener {
            val completedState = tCompletedView.isChecked
            task.setCompleted(completedState)
            taskSetCompletedCallback(task)
        }

        val tDeleteIb = convertView?.findViewById<ImageButton>(R.id.taskDeleteIb)
        tDeleteIb?.setOnClickListener {
            taskDeleteCallback(task)
        }

        val tSubscribeIb = convertView?.findViewById<ImageButton>(R.id.taskSubscribeIb)
        tSubscribeIb?.let {
            val subscriberList = task.getSubscriberList()
            if (subscriberList != null) {
                changeUserSubscribeButton(it, username, subscriberList)
            }
        }

        var direction = !task.getSubscriberList()?.contains(username)!!
        tSubscribeIb?.setOnClickListener {
            taskSubscribeCallback(task, direction
            ) { subscriberList ->
                changeUserSubscribeButton(tSubscribeIb, username, subscriberList)
                direction = !direction
            }
        }

        val taskDateTimeInterval: String
        var taskDateFrom: String? = task.getDateFrom()

        taskDateFrom = taskDateFrom ?: "??"
        var taskTimeFrom: String? = task.getTimeFrom()
        taskTimeFrom = taskTimeFrom ?: "??"
        var taskDateTo: String? = task.getDateTo()
        taskDateTo = taskDateTo ?: "??"
        var taskTimeTo: String? = task.getTimeTo()
        taskTimeTo = taskTimeTo ?: "??"
        taskDateTimeInterval =
            "$taskDateFrom, $taskTimeFrom - $taskDateTo, $taskTimeTo"
        tNameView?.text = task.getName()
        tCommentView?.text = task.getComment()
        tDateTimeInterval?.text = taskDateTimeInterval
        tCompletedView?.isChecked = task.getCompleted()!!

        return convertView
    }

    private fun changeUserSubscribeButton(
        button: ImageButton,
        username: String,
        subscriberList: List<String>) {
        if (subscriberList.contains(username)) {
            button.setImageResource(R.drawable.subscribe_off)
        } else {
            button.setImageResource(R.drawable.subscribe_on)
        }
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }


}