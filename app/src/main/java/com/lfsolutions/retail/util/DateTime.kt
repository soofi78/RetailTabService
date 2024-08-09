package com.videotel.digital.util

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.lfsolutions.retail.util.Logger
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * Created by faheem on 7/12/2017.
 */
object DateTime {
    const val DateFormat = "MMM dd, yyyy"
    const val DateFormatRetail = "yyyy-MM-dd"
    const val DateTimetRetailFormat = "yyyy-MM-dd HH:mm:ss"
    const val DateTimeRetailFrontEndFormate = "dd MMM yyyy, hh:mm a"
    const val PinFormate = "MMddyy"
    const val TimeFormat = "hh:mm a"
    const val TimeFormat24 = "HH:mm:ss"
    const val DateTimeFormat = "MMM dd, yyyy - hh:mm a"
    const val DateTimeFormatWOss = "dd/MM/yyyy HH:mm"
    const val DateFormatWithDayName = "EEEE, dd MMM, yyyy"
    const val DateFormatWithDayNameMonthNameAndTime = "EEEE, dd MMM HH:mm"
    const val SimpleDateFormat = "dd-MM-yyyy"
    const val ServerDateTimeFormat = "yyyy-MM-dd HH:mm:ss"
    fun format(STAMP: Date?, FORMAT: String?): String? {
        val from = SimpleDateFormat(FORMAT)
        var formattedDate: String? = null
        try {
            formattedDate = from.format(STAMP)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return formattedDate
    }

    fun formatServerDateTime(serverFormat: String): String {
        val sdf = SimpleDateFormat(ServerDateTimeFormat)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            val date = sdf.parse(serverFormat)
            println(date)
            SimpleDateFormat(DateTimeFormat).format(date)
        } catch (e: ParseException) {
            Logger.log(Logger.EXCEPTION, e)
            serverFormat
        }
    }

    fun formatServerDateTime(serverFormat: String, format: String?): String {
        val sdf = SimpleDateFormat(ServerDateTimeFormat)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            val date = sdf.parse(serverFormat)
            println(date)
            SimpleDateFormat(format).format(date)
        } catch (e: ParseException) {
            Logger.log(Logger.EXCEPTION, e)
            serverFormat
        }
    }

    fun formatServerTime(serverFormat: String): String {
        val sdf = SimpleDateFormat(ServerDateTimeFormat)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            val date = sdf.parse(serverFormat)
            println(date)
            SimpleDateFormat(TimeFormat).format(date)
        } catch (e: ParseException) {
            Logger.log(Logger.EXCEPTION, e)
            serverFormat
        }
    }

    fun formatServerDate(serverFormat: String): String {
        val sdf = SimpleDateFormat(ServerDateTimeFormat)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            val date = sdf.parse(serverFormat)
            println(date)
            SimpleDateFormat(DateFormat).format(date)
        } catch (e: ParseException) {
            Logger.log(Logger.EXCEPTION, e)
            serverFormat
        }
    }

    fun getCurrentDateTimeUTC(formate: String?): String {
        val sdf = SimpleDateFormat(formate)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    fun getCurrentDateTime(formate: String?): String {
        val sdf = SimpleDateFormat(formate)
        return sdf.format(Date())
    }

    @JvmStatic
    val currentDateTime: String
        get() {
            val from =
                SimpleDateFormat(DateTimeFormat)
            from.timeZone = TimeZone.getDefault()
            return from.format(Date())
        }

    @JvmStatic
    fun getFormatedDateTime(STAMP: String?): String? {
        val from = SimpleDateFormat(DateTimeFormat)
        val to = SimpleDateFormat(DateFormat + " " + TimeFormat)
        var formattedDate: String? = null
        try {
            formattedDate = to.format(from.parse(STAMP))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return formattedDate
    }

    fun convert24To12(STAMP: String?): String? {
        val from = SimpleDateFormat("hh:mm:ss")
        val to = SimpleDateFormat(TimeFormat)
        var formattedDate: String? = null
        try {
            formattedDate = to.format(from.parse(STAMP))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return formattedDate
    }

    val timeStamp: String
        get() = System.currentTimeMillis().toString() + ""

    @JvmStatic
    val timeStampForKey: String
        get() = (System.currentTimeMillis() / 1000).toString() + ""

    fun firstIsEqualesSecond(date1: String?, date2: String?): Boolean {
        val sdf = SimpleDateFormat(DateFormat)
        var first: Date? = null
        var second: Date? = null
        return try {
            first = sdf.parse(date1)
            second = sdf.parse(date2)
            if (first == second) {
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun showDatePicker(activity: Activity?, callback: OnDatePickedCallback?) {
        val yy: Int
        val mm: Int
        val dd: Int
        val calendar = Calendar.getInstance()
        yy = calendar[Calendar.YEAR]
        mm = calendar[Calendar.MONTH]
        dd = calendar[Calendar.DATE]
        val dialog = DatePickerDialog(activity!!, { view, year, monthOfYear, dayOfMonth ->
            var monthOfYear = monthOfYear
            monthOfYear = monthOfYear + 1
            var d = dayOfMonth.toString() + ""
            var m = monthOfYear.toString() + ""
            if (monthOfYear + "".length == 1) {
                m = "0$monthOfYear"
            } else if (dayOfMonth + "".length == 1) {
                d = "0$dayOfMonth"
            }
            callback?.onDateSelected(year.toString(), m, d)
        }, yy, mm, dd)
        dialog.setOnCancelListener { dialog ->
            dialog.dismiss()
            callback?.onDateSelected("", "", "")
        }
        dialog.show()
    }

    fun showTimePicker(
        activity: Activity?,
        callback: OnDatePickedCallback?,
        is24HourView: Boolean = false
    ) {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mcurrentTime[Calendar.MINUTE]
        val mTimePicker = TimePickerDialog(
            activity,
            { timePicker, selectedHour, selectedMinute ->
                callback?.onTimeSelected(
                    selectedHour.toString(),
                    selectedMinute.toString(),
                    "00"
                )
            },
            hour,
            minute,
            is24HourView
        )
        mTimePicker.setOnCancelListener { dialog ->
            dialog.dismiss()
            callback?.onTimeSelected("00", "00", "00")
        }
        mTimePicker.show()
    }

    fun convertServerTimeToCalender(stamp: String?): Calendar {
        val sdf = SimpleDateFormat(ServerDateTimeFormat)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance()
        try {
            calendar.timeInMillis = sdf.parse(stamp).time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return calendar
    }

    fun convertServerTimeToCalenderDefaultTimeZone(stamp: String?): Calendar {
        val sdf = SimpleDateFormat(ServerDateTimeFormat)
        sdf.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance()
        try {
            calendar.timeInMillis = sdf.parse(stamp).time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return calendar
    }

    fun calculateHoursMinutesFrom(start: Calendar, end: Calendar): String {
        var difference = (end.timeInMillis - start.timeInMillis) / 1000
        val hours = difference.toInt() / 3600
        difference = difference % 3600
        val minuts = difference.toInt() / 60
        val seconds = difference.toInt() % 60
        return if (hours > 0) {
            hours.toString() + "h " + minuts + "m " + seconds + "s"
        } else if (minuts == 0) {
            "$seconds sec"
        } else if (minuts == 1) {
            "$minuts min $seconds sec"
        } else if (minuts > 1) {
            "$minuts mins $seconds sec"
        } else {
            "0 min"
        }
    }

    fun getDateFromString(date: String?, formate: String?): Date {
        val sdf = SimpleDateFormat(formate)
        return try {
            sdf.parse(date)
        } catch (e: Exception) {
            Date()
        }
    }

    fun formatJourneyTime(stamp: String?): String? {
        return if (stamp != null) {
            val from = SimpleDateFormat("dd/mm/yyyy hh:mm:ss")
            val to = SimpleDateFormat(DateTimeFormat)
            var formattedDate: String? = null
            formattedDate = try {
                to.format(from.parse(stamp))
            } catch (e: Exception) {
                formatServerDateTime(stamp)
            }
            formattedDate
        } else {
            "N/A"
        }
    }

    fun formateJourneyTime(stamp: String?, format: String?): String? {
        return if (stamp != null) {
            val from = SimpleDateFormat(DateTimeFormat)
            val to = SimpleDateFormat(format)
            var formattedDate: String? = null
            formattedDate = try {
                to.format(from.parse(stamp))
            } catch (e: Exception) {
                formatServerDateTime(stamp, format)
            }
            formattedDate
        } else {
            "N/A"
        }
    }

    interface OnDatePickedCallback {
        fun onDateSelected(year: String, month: String, day: String) {}
        fun onTimeSelected(hours: String, minutes: String, seconds: String) {}
    }
}