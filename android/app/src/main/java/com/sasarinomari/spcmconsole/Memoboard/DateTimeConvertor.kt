package com.sasarinomari.spcmconsole.Memoboard

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DateTimeConvertor {
    companion object {
        fun buildDate(year: Int?, month: Int?, date: Int?): String? {
            if (year == null && month == null && date == null) return null

            val y = year ?: 0
            val m = month ?: 0
            val d = date ?: 0

            return "$y-${if (m > 9) m.toString() else "0$m"}-${if (d > 9) d.toString() else "0$d"}"
        }

        fun buildTime(hourOfDay: Int?, minute: Int?): String? {
            if (hourOfDay == null && minute == null) return null

            val h = hourOfDay ?: 0
            val m = minute ?: 0

            return "${if (h > 9) h.toString() else "0$h"}:${if (m > 9) m.toString() else "0$m"}:00"
        }

        fun formatDate(date: String): String {
            return date.substring(0, 10)
        }

        fun formatTime(time: String): String {
            return time.substring(0, time.length-3)
        }




        fun ld2d(localDate: LocalDate): Date {
            return Timestamp.valueOf(localDate.toString()) as Date
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun parseLocalDate(date: String): LocalDate {
            return LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
        }

        fun parseDate(date: String): Date {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val ld = parseLocalDate(date)
                ld2d(ld)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        }

        fun parseTime(time: String): Time {
            TODO("Not yet implemented")
        }
    }



}