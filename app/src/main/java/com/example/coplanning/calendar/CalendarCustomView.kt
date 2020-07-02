package com.example.coplanning.calendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.coplanning.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarCustomView : LinearLayout {
    private var previousButton: ImageView? = null
    private var nextButton: ImageView? = null
    private var currentDate: TextView? = null
    private var calendarGridView: GridView? = null
    private val formatter =
        SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private var mAdapter: CalendarGridAdapter? = null
    private var weekDaysMap: Map<*, *>? = null
    private var firstWeekDayNumber = 1

    private var calendarCellClick: ((Calendar) -> Unit)? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initializeUILayout()
        setUpCalendarAdapter()
        setPreviousButtonClickEvent()
        setNextButtonClickEvent()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setICalendarCellClick(calendarCellClickCallback: (Calendar)->Unit) {
        calendarCellClick = calendarCellClickCallback
        setUpCalendarAdapter()
    }

    private fun initializeUILayout() {
        val inflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.calendar_layout, this)
        createWeekDaysTextViews()
        previousButton =
            view.findViewById<View>(R.id.previous_month) as ImageView
        nextButton =
            view.findViewById<View>(R.id.next_month) as ImageView
        currentDate = view.findViewById<View>(R.id.display_current_date) as TextView
        calendarGridView = view.findViewById<View>(R.id.calendar_grid) as GridView

    }

    private fun setPreviousButtonClickEvent() {
        previousButton!!.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            setUpCalendarAdapter()
        }
    }

    private fun setNextButtonClickEvent() {
        nextButton!!.setOnClickListener {
            cal.add(Calendar.MONTH, 1)
            setUpCalendarAdapter()
        }
    }

    private fun createWeekDaysTextViews() {
        weekDaysMap = WeekDaysMap.createMap()
        val layout = findViewById<LinearLayout>(R.id.weekDays)
        fillWeekDaysByFirstWeekDay(layout)
    }

    private fun fillWeekDaysByFirstWeekDay(layout: LinearLayout): LinearLayout {
        if (firstWeekDayNumber > daysInWeek) return layout
        layout.removeAllViews()
        for (i in firstWeekDayNumber until daysInWeek) {
            createSingleWeekDayTextView(resources.getString(weekDaysMap!![i] as Int), layout)
        }
        for (i in 0 until firstWeekDayNumber) {
            createSingleWeekDayTextView(resources.getString(weekDaysMap!![i] as Int), layout)
        }
        return layout
    }

    private fun createSingleWeekDayTextView(day: String, layout: LinearLayout): LinearLayout {
        val tv = TextView(context)
        val lp = LayoutParams(
            0,  // Width of TextView
            LayoutParams.WRAP_CONTENT
        )
        lp.weight = 1f
        lp.topMargin = 4
        tv.layoutParams = lp
        tv.gravity = Gravity.CENTER
        tv.setTextColor(Color.WHITE)
        tv.text = day
        layout.addView(tv)
        return layout
    }

    private fun setUpCalendarAdapter() {
        val dayValueInCells: MutableList<Date> =
            ArrayList()
        val mCal = cal.clone() as Calendar
        mCal[Calendar.DAY_OF_MONTH] = 1
        val firstDayOfTheMonth =
            mCal[Calendar.DAY_OF_WEEK] - 1 - firstWeekDayNumber
        Log.d("MyLog", firstDayOfTheMonth.toString())
        if (firstDayOfTheMonth >= 0) mCal.add(
            Calendar.DAY_OF_MONTH,
            -firstDayOfTheMonth
        ) else {
            mCal.add(
                Calendar.DAY_OF_MONTH,
                -firstDayOfTheMonth - daysInWeek
            )
        }
        while (dayValueInCells.size < MAX_CALENDAR_COLUMN) {
            dayValueInCells.add(mCal.time)
            mCal.add(Calendar.DAY_OF_MONTH, 1)
        }
        for (date in dayValueInCells) {
            Log.d("MyLog", date.toString())
        }
        val sDate = formatter.format(cal.time)
        currentDate!!.text = sDate
        mAdapter = CalendarGridAdapter(context, dayValueInCells, cal, calendarCellClick)
        calendarGridView!!.adapter = mAdapter
    }

    companion object {
        private const val daysInWeek = 7
        private const val MAX_CALENDAR_COLUMN = 42
    }
}