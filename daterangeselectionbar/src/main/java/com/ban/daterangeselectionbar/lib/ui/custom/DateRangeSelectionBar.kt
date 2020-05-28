package com.ban.daterangeselectionbar.lib.ui.custom

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.ban.daterangeselectionbar.lib.R
import com.ban.daterangeselectionbar.lib.model.DRSDatePeriod
import com.ban.daterangeselectionbar.lib.ui.adapter.DRSDatePeriodsAdapter
import com.ban.daterangeselectionbar.lib.ui.adapter.DRSOnDatePeriodItemListener
import com.ban.daterangeselectionbar.lib.util.DRSDatePeriodsUtil
import com.ban.daterangeselectionbar.lib.util.DRSSnapOnScrollListener
import com.ban.daterangeselectionbar.lib.util.attachSnapHelperWithListener
import com.ban.daterangeselectionbar.lib.util.dateperiodsnaphelper.DRSCenterItemDecoration
import com.ban.daterangeselectionbar.lib.util.dateperiodsnaphelper.DRSExpandedSnapHelper
import kotlinx.android.synthetic.main.drs_recycler_view_date_period.view.*
import java.util.*

enum class DRSDatePeriodType{
    DRSDailyType,
    DRSWeeklyType,
    DRSMonthlyType,
    DRSYearlyType,
    DRSCustomType
}

class DateRangeSelectionBar @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attributeSet, defStyleAttr),
    DRSSnapOnScrollListener.DRSOnSnapPositionChangeListener,
    DRSOnDatePeriodItemListener {

    interface DRSOnTimePeriodSelectListener {
        fun onDatePeriodSelected(datePeriod: DRSDatePeriod)
    }

    var onTimePeriodSelectListener: DRSOnTimePeriodSelectListener? = null
    var localeForDateFormat: Locale? = null

    var currentShownTimePeriodType = DRSDatePeriodType.DRSMonthlyType
        private set

    var isCustomList = false

    var allowFutureDates = true
        set(value) {
            waitForFirstFutureDatesToLoad = value
            field = value
        }
    private var waitForFirstFutureDatesToLoad = true

    private val datePeriodList = mutableListOf<DRSDatePeriod>()
    private val datePeriodsAdapter by lazy {
        DRSDatePeriodsAdapter(
            datePeriodList,
            this
        )
    }

    private val snapHelper = DRSExpandedSnapHelper()

    fun setCurrentShownTimePeriodType(datePeriodType: DRSDatePeriodType, customPeriods: List<DRSDatePeriod>? = null, scrollToStart:Boolean = true) {
        currentShownTimePeriodType = datePeriodType
        setDatePeriodList(customPeriods = customPeriods, scrollToStart = scrollToStart)
    }

    init {
        inflate(context, R.layout.drs_recycler_view_date_period, this)

        val properties = context.obtainStyledAttributes(attributeSet, R.styleable.DateRangeSelectionBar)
        configureUI(properties)
        properties.recycle()
    }

    private fun configureUI(properties: TypedArray? = null) {
        properties?.let {
            for (i in 0 until it.indexCount) {
                val attr = it.getIndex(i)
                when (attr) {
                    R.styleable.DateRangeSelectionBar_drsUnderlineColor -> {
                        etRVDPUnderlineView.setBackgroundColor(
                            properties.getColor(attr, ContextCompat.getColor(context, R.color.colorStandardText))
                        )
                    }
                    R.styleable.DateRangeSelectionBar_drsVerticalPadding -> {
                        datePeriodsAdapter.mTextVerticalPaddingPx =
                            properties.getDimensionPixelSize(attr, resources.getDimensionPixelSize(R.dimen.drs_item_date_period_vertical_padding))
                    }
                    R.styleable.DateRangeSelectionBar_drsTextColor -> {
                        datePeriodsAdapter.mTextColorResId = properties.getResourceId(attr, R.color.colorStandardText)
                    }
                    R.styleable.DateRangeSelectionBar_drsTextSize -> {
                        datePeriodsAdapter.mTextSize = properties.getDimension(attr, resources.getDimension(R.dimen.drs_standard_text_size))
                    }
                }
            }
        }

        configureUnderlineViewWidth()

        etRVDPRecyclerView.addItemDecoration(
            DRSCenterItemDecoration(
                0,
                true
            )
        )
        etRVDPRecyclerView.adapter = datePeriodsAdapter
        setDatePeriodList()
        etRVDPRecyclerView.attachSnapHelperWithListener(snapHelper, this)
    }

    private fun setDatePeriodList(offset: Int? = null, futureDates: Boolean = false, customPeriods: List<DRSDatePeriod>? = null, scrollToStart:Boolean = true) {
        if (offset == null) datePeriodList.clear()

        setLocaleLanguage()

        val prevSize = datePeriodList.size
        val pivotDateString = when {
            datePeriodList.isEmpty() -> null
            futureDates -> datePeriodList[0].endDate
            else -> datePeriodList[datePeriodList.size - 1].startDate
        }

        datePeriodList.addAll(
                if (futureDates) 0 else datePeriodList.size,
                when (currentShownTimePeriodType) {
                    DRSDatePeriodType.DRSDailyType -> DRSDatePeriodsUtil.getDays(
                        localeForDateFormat = localeForDateFormat!!,
                        pivotDateServerFormat = pivotDateString,
                        futureDays = futureDates)
                    DRSDatePeriodType.DRSWeeklyType -> DRSDatePeriodsUtil.getWeeks(
                        localeForDateFormat = localeForDateFormat!!,
                        pivotDateServerFormat = pivotDateString,
                        futureWeeks = futureDates)
                    DRSDatePeriodType.DRSMonthlyType -> DRSDatePeriodsUtil.getMonths(
                        localeForDateFormat = localeForDateFormat!!,
                        pivotDateServerFormat = pivotDateString,
                        futureMonths = futureDates)
                    DRSDatePeriodType.DRSYearlyType -> DRSDatePeriodsUtil.getYears(
                        localeForDateFormat = localeForDateFormat!!,
                        pivotDateServerFormat = pivotDateString,
                        futureYears = futureDates)
                    DRSDatePeriodType.DRSCustomType -> customPeriods ?: mutableListOf()
                }
        )

        etRVDPRecyclerView.post {
            datePeriodsAdapter.notifyDataSetChanged()
            if (offset == null && scrollToStart) {
                snapHelper.scrollTo(0, false)
                onSnapPositionChange(0)
            } else if (futureDates && prevSize != 0) {
                snapHelper.scrollTo(datePeriodList.size - prevSize + 1, false)
                onSnapPositionChange(datePeriodList.size - prevSize)
            }
        }
    }

    private fun configureUnderlineViewWidth() {
        val deviceWidth: Int = Resources.getSystem().displayMetrics.widthPixels

        val layoutParams = etRVDPUnderlineView.layoutParams
        layoutParams.width = (deviceWidth / 3) - (2 * resources.getDimensionPixelSize(R.dimen.drs_item_date_period_horizontal_padding))
        etRVDPUnderlineView.layoutParams = layoutParams
    }

    private fun setLocaleLanguage() {
        if (localeForDateFormat == null) localeForDateFormat = resources.configuration.locale
    }

    override fun datePeriodClicked(position: Int, startDate: String, endDate: String) {
        snapHelper.scrollTo(position, true)
    }

    override fun datePeriodReachedFirst() {
        if (!isCustomList && allowFutureDates) setDatePeriodList(offset = datePeriodList.size, futureDates = true)
    }
    
    override fun datePeriodReachedLast() {
        if (!isCustomList) setDatePeriodList(offset = datePeriodList.size)
    }

    override fun onSnapPositionChange(position: Int) {
        if (position in 0 until datePeriodList.size && !waitForFirstFutureDatesToLoad) {
            onTimePeriodSelectListener?.onDatePeriodSelected(datePeriodList[position])
        }
        if (waitForFirstFutureDatesToLoad) waitForFirstFutureDatesToLoad = false
    }
}