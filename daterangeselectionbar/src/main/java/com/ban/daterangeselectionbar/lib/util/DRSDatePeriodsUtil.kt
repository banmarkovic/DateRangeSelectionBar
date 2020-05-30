package com.ban.daterangeselectionbar.lib.util

import com.ban.daterangeselectionbar.lib.model.DRSDatePeriod
import java.text.SimpleDateFormat
import java.util.*

@SuppressWarnings("SimpleDateFormat")
class DRSDatePeriodsUtil {
    companion object {
        private const val DRS_SERVER_DATE_FORMAT = "yyyy-MM-dd"
        private const val DRS_DEFAULT_APPLICATION_DATE_FORMAT = "d MMM yyyy"
        private const val DRS_DEFAULT_APPLICATION_MONTH_DATE_FORMAT = "MMM yyyy"
        private const val DRS_DEFAULT_APP_WEEK_DATE_FORMAT = "d MMM"
        private const val DRS_ONLY_YEAR_DATE_FORMAt = "yyyy"
        private const val DRS_ONLY_MONTH_DATE_FORMAT = "MMM"
        private const val DRS_ONLY_DAY_DATE_FORMAT = "d"

        fun getMonths(localeForDateFormat: Locale, pivotDateServerFormat: String?, futureMonths: Boolean = false, limit: Int = 36): List<DRSDatePeriod> {
            val monthFormat = SimpleDateFormat(DRS_DEFAULT_APPLICATION_MONTH_DATE_FORMAT, localeForDateFormat)
            val sdf = SimpleDateFormat(DRS_SERVER_DATE_FORMAT)
            val months = mutableListOf<DRSDatePeriod>()

            val cal = Calendar.getInstance()
            if (!pivotDateServerFormat.isNullOrEmpty()) {
                cal.time = sdf.parse(pivotDateServerFormat) ?: Date()
                cal.add(Calendar.MONTH, if (futureMonths) 1 else -1)
            }

            for (i in 0 until limit) {
                cal[Calendar.DATE] = 1
                val firstDateOfMonth = cal.time
                cal[Calendar.DATE] = cal.getActualMaximum(Calendar.DATE)
                val lastDateOfMonth = cal.time

                months.add(
                    if (futureMonths) 0 else months.size,
                    DRSDatePeriod(
                        monthFormat.format(firstDateOfMonth),
                        sdf.format(firstDateOfMonth),
                        sdf.format(lastDateOfMonth)
                    )
                )

                cal.add(Calendar.MONTH, if (futureMonths) 1 else -1)
            }

            return months
        }

        fun getWeeks(localeForDateFormat: Locale, pivotDateServerFormat: String?, futureWeeks: Boolean = false, limit: Int = 48): List<DRSDatePeriod> {
            val onlyMonthFormat = SimpleDateFormat(DRS_ONLY_MONTH_DATE_FORMAT, localeForDateFormat)
            val onlyDateFormat = SimpleDateFormat(DRS_ONLY_DAY_DATE_FORMAT, localeForDateFormat)

            val dateFormat = SimpleDateFormat(DRS_DEFAULT_APP_WEEK_DATE_FORMAT, localeForDateFormat)
            val sdf = SimpleDateFormat(DRS_SERVER_DATE_FORMAT)
            val weeks = mutableListOf<DRSDatePeriod>()

            val cal = Calendar.getInstance()
            if (!pivotDateServerFormat.isNullOrEmpty()) {
                cal.time = sdf.parse(pivotDateServerFormat) ?: Date()
                cal.add(Calendar.DATE, if (futureWeeks) 1 else -1)
            }

            for (i in 0 until limit) {
                var firstDateOfWeek = cal.time
                var lastDateOfWeek = cal.time

                cal.add(Calendar.DATE, if (futureWeeks) 6 else -6)
                if (futureWeeks) lastDateOfWeek = cal.time
                else firstDateOfWeek = cal.time

                var firstDateFormatted = dateFormat.format(firstDateOfWeek)
                val lastDateFormatted = dateFormat.format(lastDateOfWeek)
                if (onlyMonthFormat.format(firstDateOfWeek) == onlyMonthFormat.format(lastDateOfWeek)) {
                    firstDateFormatted = onlyDateFormat.format(firstDateOfWeek)
                }

                val showWeekFormat = "$firstDateFormatted - $lastDateFormatted"

                weeks.add(
                    if (futureWeeks) 0 else weeks.size,
                    DRSDatePeriod(
                        showWeekFormat,
                        sdf.format(firstDateOfWeek),
                        sdf.format(lastDateOfWeek)
                    )
                )

                cal.add(Calendar.DATE, if (futureWeeks) 1 else -1)
            }

            return weeks
        }

        fun getDays(localeForDateFormat: Locale, pivotDateServerFormat: String?, futureDays: Boolean = false, limit: Int = 90): List<DRSDatePeriod> {
            val dateFormat = SimpleDateFormat(DRS_DEFAULT_APPLICATION_DATE_FORMAT, localeForDateFormat)
            val sdf = SimpleDateFormat(DRS_SERVER_DATE_FORMAT, localeForDateFormat)

            val days = mutableListOf<DRSDatePeriod>()

            val cal = Calendar.getInstance()
            if (!pivotDateServerFormat.isNullOrEmpty()) {
                cal.time = sdf.parse(pivotDateServerFormat) ?: Date()
                cal.add(Calendar.DATE, if (futureDays) 1 else -1)
            }

            for (i in 0 until limit) {
                val date = cal.time
                val dateServerFormatted = sdf.format(date)

                days.add(
                    if (futureDays) 0 else days.size,
                    DRSDatePeriod(
                        dateFormat.format(date),
                        dateServerFormatted,
                        dateServerFormatted
                    )
                )

                cal.add(Calendar.DATE, if (futureDays) 1 else -1)
            }

            return days
        }

        fun getYears(localeForDateFormat: Locale, pivotDateServerFormat: String?, futureYears: Boolean = false, limit: Int = 30): List<DRSDatePeriod> {
            val yearDateFormat = SimpleDateFormat(DRS_ONLY_YEAR_DATE_FORMAt, localeForDateFormat)
            val sdf = SimpleDateFormat(DRS_SERVER_DATE_FORMAT, localeForDateFormat)

            val years = mutableListOf<DRSDatePeriod>()

            val cal = Calendar.getInstance()
            if (!pivotDateServerFormat.isNullOrEmpty()) {
                cal.time = sdf.parse(pivotDateServerFormat) ?: Date()
                cal.add(Calendar.YEAR, if (futureYears) 1 else -1)
            }

            for (i in 0 until limit) {
                cal[Calendar.MONTH] = 0
                cal[Calendar.DATE] = 1
                val yearStartDate = cal.time

                cal[Calendar.MONTH] = 11
                cal[Calendar.DATE] = 31
                val yearEndDate = cal.time

                years.add(
                    if (futureYears) 0 else years.size,
                    DRSDatePeriod(
                        yearDateFormat.format(yearEndDate),
                        sdf.format(yearStartDate),
                        sdf.format(yearEndDate)
                    )
                )

                cal.add(Calendar.YEAR, if (futureYears) 1 else -1)
            }

            return years
        }
    }
}