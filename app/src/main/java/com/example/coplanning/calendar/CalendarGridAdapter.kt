package com.example.coplanning.calendar

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.example.coplanning.R
import java.util.*

class CalendarGridAdapter(
    context: Context?,
    private val monthlyDates: List<Date>,
    private val currentDate: Calendar,
    private val calendarCellClickCallback: ((Calendar)->Unit)?
) : ArrayAdapter<Any?>(context!!, R.layout.calendar_cell_layout) {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    @NonNull
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val mDate = monthlyDates[position]
        val dateCal = Calendar.getInstance()
        dateCal.time = mDate
        val dayValue = dateCal[Calendar.DAY_OF_MONTH]
        val displayMonth = dateCal[Calendar.MONTH] + 1
        val displayYear = dateCal[Calendar.YEAR]
        val currentMonth = currentDate[Calendar.MONTH] + 1
        val currentYear = currentDate[Calendar.YEAR]
        val today: Calendar = GregorianCalendar()
        val todayYear = today[Calendar.YEAR]
        val todayMonth = today[Calendar.MONTH] + 1
        val todayDate = today[Calendar.DAY_OF_MONTH]
        var view = convertView
        if (view == null) {
            view = mInflater.inflate(R.layout.calendar_cell_layout, parent, false)
        }

        //Add day to calendar
        val cellNumber =
            view!!.findViewById<View>(R.id.calendar_date_id) as TextView
        cellNumber.text = dayValue.toString()
        if (displayMonth == todayMonth && displayYear == todayYear && dayValue == todayDate) {
            cellNumber.setTextColor(context.getColor(R.color.colorDarkRed))
        } else if (displayMonth == currentMonth && displayYear == currentYear) {
            cellNumber.setTextColor(context.getColor(R.color.colorLightRed))
        } else {
            cellNumber.setTextColor(context.getColor(R.color.colorLightGrey))
        }
        view.setOnClickListener { v ->
            val iterator: Iterator<*> =
                selectedDays.entries.iterator()
            while (iterator.hasNext()) {
                val pair =
                    iterator.next() as Map.Entry<*, *>
                val curView = pair.key as View
                val curColor = pair.value as Int

                val cellNumber =
                    curView.findViewById<View>(R.id.calendar_date_id) as TextView
                cellNumber.setTextColor(curColor)
            }

            val cellNumber =
                v.findViewById<View>(R.id.calendar_date_id) as TextView
            val prevBackgroundColor = cellNumber.currentTextColor
            cellNumber.setTextColor(Color.parseColor("#8c4c22"))
            val calendar: Calendar = GregorianCalendar()
            calendar[displayYear, displayMonth - 1] = dayValue

            calendarCellClickCallback?.invoke(calendar)

            selectedDays[v] = prevBackgroundColor
        }
        return view
    }

    fun onFragmentInteraction(uri: Uri?) {}

    override fun getCount(): Int {
        return monthlyDates.size
    }

    @Nullable
    override fun getItem(position: Int): Any? {
        return monthlyDates[position]
    }

    override fun getPosition(item: Any?): Int {
        return monthlyDates.indexOf(item)
    }

    companion object {
        private val selectedDays =
            HashMap<View, Int>()
    }

}

