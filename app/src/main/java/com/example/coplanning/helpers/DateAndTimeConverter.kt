package com.example.coplanning.helpers

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateAndTimeConverter {

    companion object {

        fun ConvertToStringDate(year: Int, month: Int, date: Int): String? {
            var strDate = date.toString()
            strDate = if (strDate.length > 1) strDate else "0$strDate"
            var strMonth = month.toString()
            strMonth = if (strMonth.length > 1) strMonth else "0$strMonth"
            val strYear = year.toString()
            return "$strDate.$strMonth.$strYear"
        }

        fun ConvertToStringTime(hours: Int, minutes: Int): String? {
            var strHours = hours.toString()
            strHours = if (strHours.length > 1) strHours else "0$strHours"
            var strMinutes = minutes.toString()
            strMinutes = if (strMinutes.length > 1) strMinutes else "0$strMinutes"
            return "$strHours-$strMinutes"
        }

        fun ConvertToISOStringDate(year: Int, month: Int, date: Int): String {
            var strDate = date.toString()
            strDate = if (strDate.length > 1) strDate else "0$strDate"
            var strMonth = month.toString()
            strMonth = if (strMonth.length > 1) strMonth else "0$strMonth"
            val strYear = year.toString()
            return "$strYear-$strMonth-$strDate"
        }

        fun ConvertToISOStringTime(hours: Int, minutes: Int): String {
            var strHours = hours.toString()
            strHours = if (strHours.length > 1) strHours else "0$strHours"
            var strMinutes = minutes.toString()
            strMinutes = if (strMinutes.length > 1) strMinutes else "0$strMinutes"
            return "$strHours:$strMinutes"
        }


        fun ConvertStringDateToISO(date: String): String? {
            val dateVals = GetDateValuesFromStringDate(date)
            val intYear = dateVals[2].toInt()
            val intMonth = dateVals[1].toInt()
            val intDate = dateVals[0].toInt()
            return ConvertToISOStringDate(intYear, intMonth, intDate)
        }

        fun ConvertStringTimeToISO(time: String): String? {
            val timeVals = time.split("-").toTypedArray()
            val intHours = timeVals[0].toInt()
            val intMinutes = timeVals[1].toInt()
            return ConvertToISOStringTime(intHours, intMinutes)
        }

        private fun GetValuesFromISODateTimeString(dateTime: String): Array<String?>? {
            return dateTime.split("'T'").toTypedArray()
        }

        private fun GetDateValuesFromStringDate(date: String): Array<String> {
            return date.split(".").toTypedArray()
        }

        private fun GetDateValuesFromISOStringDate(date: String): Array<String> {
            return date.split("-").toTypedArray()
        }

        fun GetYearFromStringDate(date: String): Int {
            val dateVals = GetDateValuesFromStringDate(date)
            return dateVals[2].toInt()
        }

        fun GetMonthFromStringDate(date: String): Int {
            val dateVals = GetDateValuesFromStringDate(date)
            return dateVals[1].toInt()
        }

        fun GetDayFromStringDate(date: String): Int {
            val dateVals = GetDateValuesFromStringDate(date)
            return dateVals[0].toInt()
        }

        fun GetYearFromISOStringDate(date: String): Int {
            val dateVals = GetDateValuesFromISOStringDate(date)
            return dateVals[0].toInt()
        }

        fun GetMonthFromISOStringDate(date: String): Int {
            val dateVals = GetDateValuesFromISOStringDate(date)
            return dateVals[1].toInt()
        }

        fun GetDayFromISOStringDate(date: String): Int {
            val dateVals = GetDateValuesFromISOStringDate(date)
            return dateVals[2].toInt()
        }


        private fun GetTimeValuesFromStringTime(time: String): Array<String> {
            return time.split("-").toTypedArray()
        }

        private fun GetTimeValuesFromISOStringTime(time: String): Array<String> {
            return time.split(":").toTypedArray()
        }

        fun GetHourFromStringTime(time: String): Int {
            val timeVals = GetTimeValuesFromStringTime(time)
            return timeVals[0].toInt()
        }

        fun GetMinutesFromStringTime(time: String): Int {
            val timeVals = GetTimeValuesFromStringTime(time)
            return timeVals[1].toInt()
        }

        fun GetHourFromISOStringTime(time: String): Int {
            val timeVals = GetTimeValuesFromISOStringTime(time)
            return timeVals[0].toInt()
        }

        fun GetMinutesFromISOStringTime(time: String): Int {
            val timeVals = GetTimeValuesFromISOStringTime(time)
            return timeVals[1].toInt()
        }

        fun GetISOStringDateFromISOString(isoDateTime: String): String? {
            val calendar = GetCalendarDateTimeFromISOString(isoDateTime)
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH] + 1
            val day = calendar[Calendar.DAY_OF_MONTH]
            return ConvertToISOStringDate(year, month, day)
        }

        fun GetISOStringTimeFromISOString(isoDateTime: String): String? {
            val calendar = GetCalendarDateTimeFromISOString(isoDateTime)
            val hours = calendar[Calendar.HOUR_OF_DAY]
            val minutes = calendar[Calendar.MINUTE]
            return ConvertToISOStringTime(hours, minutes)
        }


        fun GetCalendarDateTimeFromISOString(isoDateTime: String): Calendar {
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

        fun GetStringDateFromCalendar(calendar: Calendar): String? {
            val date = calendar[Calendar.DAY_OF_MONTH]
            val month = calendar[Calendar.MONTH]
            val year = calendar[Calendar.YEAR]
            val dispMonth = month + 1
            return DateAndTimeConverter.ConvertToStringDate(
                year,
                dispMonth,
                date
            )
        }

        fun GetStringTimeFromCalendar(calendar: Calendar): String? {
            val hours = calendar[Calendar.HOUR_OF_DAY]
            val minutes = calendar[Calendar.MINUTE]
            return DateAndTimeConverter.ConvertToStringTime(
                hours,
                minutes
            )
        }


        fun GetISOStringDateFromCalendar(calendar: Calendar): String? {
            val date = calendar[Calendar.DAY_OF_MONTH]
            val month = calendar[Calendar.MONTH]
            val year = calendar[Calendar.YEAR]
            val dispMonth = month + 1
            return DateAndTimeConverter.ConvertToISOStringDate(
                year,
                dispMonth,
                date
            )
        }

        fun GetISOStringTimeFromCalendar(calendar: Calendar): String? {
            val hours = calendar[Calendar.HOUR_OF_DAY]
            val minutes = calendar[Calendar.MINUTE]
            return DateAndTimeConverter.ConvertToISOStringTime(
                hours,
                minutes
            )
        }
    }
}