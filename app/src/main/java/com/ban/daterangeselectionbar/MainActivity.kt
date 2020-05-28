package com.ban.daterangeselectionbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ban.daterangeselectionbar.lib.model.DRSDatePeriod
import com.ban.daterangeselectionbar.lib.ui.custom.DRSDatePeriodType
import com.ban.daterangeselectionbar.lib.ui.custom.DateRangeSelectionBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val onTimePeriodSelectListener = object: DateRangeSelectionBar.DRSOnTimePeriodSelectListener {
        override fun onDatePeriodSelected(datePeriod: DRSDatePeriod) {
            Toast.makeText(this@MainActivity, "${datePeriod.startDate} - ${datePeriod.endDate}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dateRangeSelectionBar.onTimePeriodSelectListener = onTimePeriodSelectListener
        dateRangeSelectionBar.allowFutureDates = true
    }

    fun datePeriodTypeSelected(v: View) {
        dateRangeSelectionBar.setCurrentShownTimePeriodType(when (v.id) {
            R.id.daysRadioButton -> DRSDatePeriodType.DRSDailyType
            R.id.weeksRadioButton -> DRSDatePeriodType.DRSWeeklyType
            R.id.monthsRadioButton -> DRSDatePeriodType.DRSMonthlyType
            R.id.yearsRadioButton -> DRSDatePeriodType.DRSYearlyType
            else -> DRSDatePeriodType.DRSMonthlyType
        })
    }
}
