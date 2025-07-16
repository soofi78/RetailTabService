package com.lfsolutions.retail.util

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import com.whiteelephant.monthpicker.MonthPickerDialog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Created by faheem on 7/12/2017.
 */
object DateTime {
    const val DateFormat = "MMM dd, yyyy"
    const val DateFormatRetail = "yyyy-MM-dd"
    const val DateTimetRetailFormat = "yyyy-MM-dd HH:mm:ss"
    const val DateTimetRetailGSTFormat = "yyyy-MM-dd HH:mm:ss"
    const val DateTimeRetailFrontEndFormate = "dd MMM yyyy, hh:mm a"
    const val DateRetailApiFormate = "dd MMM yyyy"
    const val TimeFormat = "hh:mm a"
    const val TimeFormat24 = "HH:mm:ss"
    const val DateTimeFormat = "MMM dd, yyyy - hh:mm a"
    const val DateTimeFormatWOss = "dd/MM/yyyy HH:mm"
    const val DateFormatWithDayName = "EEEE, dd MMM, yyyy"
    const val DateFormatWithDayNameMonthNameAndTime = "EEEE, dd MMM HH:mm"
    const val DateFormatWithMonthNameAndYear = "MMM yyyy"
    const val DateFormatWithDayNameMonthNameAndYear = "EEEE, dd MMM yyyy"
    const val SimpleDateFormat = "dd-MM-yyyy"
    const val ServerDateTimeFormat = "yyyy-MM-dd HH:mm:ss"
    const val DateTimePickerFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    fun getFormattedSGTDate(input: String?): String {
        if (input == null) return ""
        val date = getDateFromString(
            input.replace("T", " ").replace("Z", ""),
            DateTimetRetailFormat
        )
        val formatted = format(date, DateFormatRetail)
        return formatted?:""
    }

    fun getFormattedSGTTime(input: String?): String {
        try {
            if (input == null) return ""

            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'"
            )

            var date: Date? = null

            for (pattern in formats) {
                val inputFormat = SimpleDateFormat(pattern, Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")

                try {
                    date = inputFormat.parse(input)
                    if (date != null) break
                } catch (e: ParseException) {
                    // Try next format
                }
            }

            if (date == null) return ""

            val outputFormat = SimpleDateFormat(DateTimetRetailGSTFormat, Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("Asia/Singapore")

            return outputFormat.format(date)
        }catch (ex:Exception){
            println("parsing date:$ex")
           return "NA"
        }
    }


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
        val sdf = SimpleDateFormat(DateTimetRetailFormat)
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

    fun getStartAndEndDate():Pair<String,String>{
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Current date
        val currentCalendar = Calendar.getInstance()
        val currentDate = isoFormat.format(currentCalendar.time)

        // One month back
        val pastCalendar = Calendar.getInstance()
        pastCalendar.add(Calendar.MONTH, -1)
        val oneMonthBack = isoFormat.format(pastCalendar.time)

        return Pair(currentDate, oneMonthBack)
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

    fun showMonthYearPicker(activity: Activity?, onDatePickedCallback: OnDatePickedCallback) {
        val today = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val builder = MonthPickerDialog.Builder(
            activity, { selectedMonth, selectedYear ->

                var month = selectedMonth.plus(1).toString()
                if (month.length == 1) {
                    month = "0$month"
                }

                onDatePickedCallback.onDateSelected(
                    selectedYear.toString(),
                    month,
                    "01"
                )
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH)
        )
        builder.setMaxYear(today.get(Calendar.YEAR) + 2)
        builder.build().show()
    }

    fun String.getFormattedDisplayDateTime(): String {
        val dateTimeObject = getDateFromString(this, DateTimePickerFormat)
        return DateTime.format(dateTimeObject, DateTimeRetailFrontEndFormate)?: getCurrentDateTime(DateTimeRetailFrontEndFormate)
    }

    fun String.extractOnlyDate(): String {
        return try {
            val inputFormat = SimpleDateFormat(DateTimePickerFormat, Locale.getDefault())
            val outputFormat = SimpleDateFormat(DateRetailApiFormate, Locale.getDefault())
            val parsedDate = inputFormat.parse(this)
            outputFormat.format(parsedDate)
        } catch (e: Exception) {
            // If parsing fails, fallback to current date
            SimpleDateFormat(DateRetailApiFormate, Locale.getDefault()).format(Date())
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun String.getDateFromString():String{
         // Define formatter matching the input pattern
        val inputFormatter = DateTimeFormatter.ofPattern(DateTimePickerFormat)
        val dateTime = ZonedDateTime.parse(this, inputFormatter)

        // Format only date
        val outputDate = dateTime.toLocalDate().format(DateTimeFormatter.ofPattern(DateRetailApiFormate))
        println("outputDate: $outputDate") // Output: 12-05-2025
        return  outputDate
    }

    fun showDatePicker(activity: Activity?, callback: OnDatePickedCallback?) {
        val yy: Int
        val mm: Int
        val dd: Int
        val calendar = Calendar.getInstance()
        yy = calendar[Calendar.YEAR]
        mm = calendar[Calendar.MONTH]
        dd = calendar[Calendar.DATE]
        val dialog =
            DatePickerDialog(activity!!, { view, year, monthOfYear, dayOfMonth ->
                val month = monthOfYear + 1
                var d = dayOfMonth.toString() + ""
                var m = month.toString() + ""
                if (month.toString().length == 1) {
                    m = "0$month"
                }

                if (dayOfMonth.toString().length == 1) {
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

    fun showDateTimePicker(activity: Activity?, callback: OnDatePickedCallback?) {
        if (activity == null) return

        showDatePicker(activity, object : OnDatePickedCallback {
            override fun onDateSelected(year: String, month: String, day: String) {
                if (year.isEmpty()) {
                    val time = getCurrentDateTime(ServerDateTimeFormat).replace(" ", "T").plus("Z")
                    val dateTimeObject = getDateFromString(time, DateTimePickerFormat)
                    val formattedDateTime =format(dateTimeObject, DateTimeRetailFrontEndFormate)
                    if (formattedDateTime != null) {
                        callback?.onDateTimeSelected(time, formattedDateTime,"$year-$month-$day")
                    }
                    return
                }

                showTimePicker(activity, object : OnDatePickedCallback {
                    override fun onTimeSelected(hour: String, minute: String, second: String) {
                        // Combine date and time
                        // Combine into raw date time string like: "yyyy-MM-dd HH:mm:ss"
                        val rawDateTime = "$year-$month-$day $hour:$minute:$second"

                        // Apply your custom DateTime class logic
                        val time = rawDateTime.replace(" ", "T") + "Z"
                        //println("time $time")
                        val dateTimeObject = getDateFromString(time, DateTimePickerFormat)
                        val displayDateTime = format(dateTimeObject, DateTimeRetailFrontEndFormate)
                         println("formattedDateTime2 $displayDateTime")
                        if (displayDateTime != null) {
                            callback?.onDateTimeSelected(time, displayDateTime,"$year-$month-$day")
                        }else{
                            callback?.onDateTimeSelected(time, time,"$year-$month-$day")
                        }
                    }

                    override fun onDateSelected(year: String, month: String, day: String) {}
                }, is24HourView = true)
            }

            override fun onTimeSelected(hours: String, minutes: String, seconds: String) {}
        })
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

    fun getTimeFromDateString(date: String?): String {
        return try {
            // Parse the ISO 8601 format string
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // input is in UTC

            val outputFormat = SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH)
            outputFormat.timeZone = TimeZone.getTimeZone("UTC") // output also in UTC

            val time = inputFormat.parse(date)
            return outputFormat.format(time!!)
        } catch (e: Exception) {
            ""
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
        fun onDateTimeSelected(isoDateTime: String, displayDateTime: String,date:String) {}
    }
}