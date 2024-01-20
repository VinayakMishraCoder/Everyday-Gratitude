package com.example.everydaygraditude.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {

    val DATE_FORMAT_1 = "dd/MM/yyyy"
    val DATE_FORMAT_2 = "MMMM d'th"

    fun getCurrentDate() : String {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat(DATE_FORMAT_1)
        return sdf.format(calendar.time)
    }

    fun getPreviousDate(currentDateString: String, dateFormat: String): String {
        val sdf = SimpleDateFormat(dateFormat)
        val currentDate: Date = sdf.parse(currentDateString)

        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_MONTH, -1) // Subtract 1 day to get the previous date

        return sdf.format(calendar.time)
    }

    fun convertDatePattern(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date: Date = inputFormat.parse(inputDate) ?: return ""
            val outputFormat = SimpleDateFormat("MMMM d", Locale.getDefault())

            val formattedDateWithoutSuffix = outputFormat.format(date)

            // Append the 'th' suffix to the day of the month
            val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(date).toInt()
            val suffix = getDayOfMonthSuffix(dayOfMonth)
            formattedDateWithoutSuffix + suffix

        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun getDayOfMonthSuffix(n: Int): String {
        if (n in 11..13) {
            return "th"
        }
        return when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
}