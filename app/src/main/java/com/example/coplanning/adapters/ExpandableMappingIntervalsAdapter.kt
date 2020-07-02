package com.example.coplanning.adapters

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.annotation.NonNull
import com.example.coplanning.R
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.mapping.MappingResultItem
import com.example.coplanning.models.mapping.MappingResultsByGroups

class ExpandableMappingIntervalsAdapter(
    private val context: Context,
    private val groupsList: List<String>,
    private val mappingResultsCollection: List<MappingResultsByGroups>,
    private val groupLayout: Int,
    private val mappingIntervalLayout: Int
    ) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return groupsList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return mappingResultsCollection[groupPosition].mappingResults.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return groupsList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return mappingResultsCollection[groupPosition].mappingResults[childPosition]
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
        val groupTitleTv = convertView?.findViewById<TextView>(R.id.groupName)
        groupTitleTv?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        groupTitleTv?.text = groupTitle
        return convertView
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        val curMappingResultElement = getChild(groupPosition, childPosition) as MappingResultItem
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(mappingIntervalLayout, null)
        }
        val dateTimeFrom= curMappingResultElement.dateTimeFrom
        val dateTimeTo = curMappingResultElement.dateTimeTo
        val dateFrom = dateTimeFrom.let { DateAndTimeConverter.getStringDateFromCalendar(it) }
        val timeFrom = dateTimeFrom.let { DateAndTimeConverter.getISOStringTimeFromCalendar(it) }
        val dateTo = dateTimeTo.let { DateAndTimeConverter.getStringDateFromCalendar(it) }
        val timeTo = dateTimeTo.let { DateAndTimeConverter.getISOStringTimeFromCalendar(it) }
        val timeFromTv = convertView?.findViewById<TextView>(R.id.timeFrom)
        val timeToTv = convertView?.findViewById<TextView>(R.id.timeTo)
        if (dateFrom == dateTo) {
            timeFromTv?.text = timeFrom
            timeToTv?.text = timeTo
        } else {
            timeFromTv?.text = "$dateFrom, $timeFrom"
            timeToTv?.text = "$dateTo, $timeTo"
        }

        val usersListTv = convertView?.findViewById<TextView>(R.id.usersList)
        val users = curMappingResultElement.users
        var usersView = ""
        for (user in users) {
            usersView += "$user "
        }

        val spannableStringBuilder = SpannableStringBuilder(usersView)
        for (user in users) {
            val curSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(@NonNull widget: View) {}
            }
            val indexOfCurUser = usersView.indexOf(user)
            spannableStringBuilder.setSpan(
                curSpan,
                indexOfCurUser,
                indexOfCurUser + user.length,
                Spanned.SPAN_COMPOSING
            )
        }
        usersListTv?.text = spannableStringBuilder
        usersListTv?.movementMethod = LinkMovementMethod.getInstance()
        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

}