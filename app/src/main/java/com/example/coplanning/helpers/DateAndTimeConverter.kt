package com.example.coplanning.helpers

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateAndTimeConverter {

    companion object {
        fun convertToStringDate(year: Int, month: Int, date: Int): String? {
            var strDate = date.toString()
            strDate = if (strDate.length > 1) strDate else "0$strDate"
            var strMonth = month.toString()
            strMonth = if (strMonth.length > 1) strMonth else "0$strMonth"
            val strYear = year.toString()
            return "$strDate.$strMonth.$strYear"
        }

        fun convertToStringTime(hours: Int, minutes: Int): String? {
            var strHours = hours.toString()
            strHours = if (strHours.length > 1) strHours else "0$strHours"
            var strMinutes = minutes.toString()
            strMinutes = if (strMinutes.length > 1) strMinutes else "0$strMinutes"
            return "$strHours-$strMinutes"
        }

        fun convertToISOStringDate(year: Int, month: Int, date: Int): String {
            var strDate = date.toString()
            strDate = if (strDate.length > 1) strDate else "0$strDate"
            var strMonth = month.toString()
            strMonth = if (strMonth.length > 1) strMonth else "0$strMonth"
            val strYear = year.toString()
            return "$strYear-$strMonth-$strDate"
        }

        fun convertToISOStringTime(hours: Int, minutes: Int): String {
            var strHours = hours.toString()
            strHours = if (strHours.length > 1) strHours else "0$strHours"
            var strMinutes = minutes.toString()
            strMinutes = if (strMinutes.length > 1) strMinutes else "0$strMinutes"
            return "$strHours:$strMinutes"
        }


        fun convertStringDateToISO(date: String): String? {
            val dateVals = getDateValuesFromStringDate(date)
            val intYear = dateVals[2].toInt()
            val intMonth = dateVals[1].toInt()
            val intDate = dateVals[0].toInt()
            return convertToISOStringDate(intYear, intMonth, intDate)
        }

        fun convertStringTimeToISO(time: String): String? {
            val timeVals = time.split("-").toTypedArray()
            val intHours = timeVals[0].toInt()
            val intMinutes = timeVals[1].toInt()
            return convertToISOStringTime(intHours, intMinutes)
        }

        private fun getDateValuesFromStringDate(date: String): Array<String> {
            return date.split(".").toTypedArray()
        }

        private fun getDateValuesFromISOStringDate(date: String): Array<String> {
            return date.split("-").toTypedArray()
        }

        fun getYearFromStringDate(date: String): Int {
            val dateVals = getDateValuesFromStringDate(date)
            return dateVals[2].toInt()
        }

        fun getMonthFromStringDate(date: String): Int {
            val dateVals = getDateValuesFromStringDate(date)
            return dateVals[1].toInt()
        }

        fun getDayFromStringDate(date: String): Int {
            val dateVals = getDateValuesFromStringDate(date)
            return dateVals[0].toInt()
        }

        fun getYearFromISOStringDate(date: String): Int {
            val dateVals = getDateValuesFromISOStringDate(date)
            return dateVals[0].toInt()
        }

        fun getMonthFromISOStringDate(date: String): Int {
            val dateVals = getDateValuesFromISOStringDate(date)
            return dateVals[1].toInt()
        }

        fun getDayFromISOStringDate(date: String): Int {
            val dateVals = getDateValuesFromISOStringDate(date)
            return dateVals[2].toInt()
        }


        private fun getTimeValuesFromStringTime(time: String): Array<String> {
            return time.split("-").toTypedArray()
        }

        private fun getTimeValuesFromISOStringTime(time: String): Array<String> {
            return time.split(":").toTypedArray()
        }

        fun getHourFromStringTime(time: String): Int {
            val timeVals = getTimeValuesFromStringTime(time)
            return timeVals[0].toInt()
        }

        fun getMinutesFromStringTime(time: String): Int {
            val timeVals = getTimeValuesFromStringTime(time)
            return timeVals[1].toInt()
        }

        fun getHourFromISOStringTime(time: String): Int {
            val timeVals = getTimeValuesFromISOStringTime(time)
            return timeVals[0].toInt()
        }

        fun getMinutesFromISOStringTime(time: String): Int {
            val timeVals = getTimeValuesFromISOStringTime(time)
            return timeVals[1].toInt()
        }

        fun getISOStringDateFromISOString(isoDateTime: String): String? {
            val calendar = getCalendarDateTimeFromISOString(isoDateTime)
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH] + 1
            val day = calendar[Calendar.DAY_OF_MONTH]
            return convertToISOStringDate(year, month, day)
        }

        fun getISOStringTimeFromISOString(isoDateTime: String): String? {
            val calendar = getCalendarDateTimeFromISOString(isoDateTime)
            val hours = calendar[Calendar.HOUR_OF_DAY]
            val minutes = calendar[Calendar.MINUTE]
            return convertToISOStringTime(hours, minutes)
        }


        fun getCalendarDateTimeFromISOString(isoDateTime: String): Calendar {
            val cal = Calendar.getInstance()
            val df = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            )
            df.timeZone = TimeZone.getTimeZone("UTC")
            var dateFrom: Date? = null
            try {
                dateFrom = df.parse(isoDateTime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            cal.time = dateFrom
            return cal
        }

        fun getStringDateFromCalendar(calendar: Calendar): String? {
            val date = calendar[Calendar.DAY_OF_MONTH]
            val month = calendar[Calendar.MONTH]
            val year = calendar[Calendar.YEAR]
            val dispMonth = month + 1
            return convertToStringDate(
                year,
                dispMonth,
                date
            )
        }

        fun getISOStringDateFromCalendar(calendar: Calendar): String? {
            val date = calendar[Calendar.DAY_OF_MONTH]
            val month = calendar[Calendar.MONTH]
            val year = calendar[Calendar.YEAR]
            val dispMonth = month + 1
            return convertToISOStringDate(
                year,
                dispMonth,
                date
            )
        }

        fun getISOStringTimeFromCalendar(calendar: Calendar): String? {
            val hours = calendar[Calendar.HOUR_OF_DAY]
            val minutes = calendar[Calendar.MINUTE]
            return convertToISOStringTime(
                hours,
                minutes
            )
        }
    }
}