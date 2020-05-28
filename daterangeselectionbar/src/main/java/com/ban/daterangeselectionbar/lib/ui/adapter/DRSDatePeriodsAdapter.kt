package com.ban.daterangeselectionbar.lib.ui.adapter

import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ban.daterangeselectionbar.lib.R
import com.ban.daterangeselectionbar.lib.model.DRSDatePeriod
import kotlinx.android.synthetic.main.drs_item_date_period.view.*


class DRSDatePeriodsAdapter(
    private val data: List<DRSDatePeriod>,
    private val itemListener: DRSOnDatePeriodItemListener?
) : RecyclerView.Adapter<DRSDatePeriodsAdapter.ETViewHolder>() {

    var mTextVerticalPaddingPx: Int? = null
    var mTextColorResId = R.color.colorStandardText
    var mTextSize: Float? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ETViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.drs_item_date_period, parent, false)

        val deviceWidth: Int = Resources.getSystem().displayMetrics.widthPixels

        val layoutParams = itemView.layoutParams
        layoutParams.width = deviceWidth / 3
        itemView.layoutParams = layoutParams

        return ETViewHolder(itemView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ETViewHolder, position: Int) {
        holder.bind(data[position], position, data.size, itemListener)
    }

    inner class ETViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: DRSDatePeriod, position: Int, dataSize: Int, itemListener: DRSOnDatePeriodItemListener?) = with(itemView) {
            etIDPText.apply {
                setPadding(
                    resources.getDimensionPixelSize(R.dimen.drs_item_date_period_horizontal_padding),
                    mTextVerticalPaddingPx ?: resources.getDimensionPixelSize(R.dimen.drs_item_date_period_vertical_padding),
                    resources.getDimensionPixelSize(R.dimen.drs_item_date_period_horizontal_padding),
                    mTextVerticalPaddingPx ?: resources.getDimensionPixelSize(R.dimen.drs_item_date_period_vertical_padding)
                )
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    mTextSize ?: resources.getDimension(R.dimen.drs_standard_text_size)
                )
                setTextColor(ContextCompat.getColor(context, mTextColorResId))
                text = item.datePeriodLabelShown
            }

            setOnClickListener {
                itemListener?.datePeriodClicked(position, item.startDate, item.endDate)
            }

            when (position) {
                0 -> itemListener?.datePeriodReachedFirst()
                dataSize - 1 -> itemListener?.datePeriodReachedLast()
                else -> {}
            }
        }
    }
}

interface DRSOnDatePeriodItemListener {
    fun datePeriodClicked(position: Int, startDate: String, endDate: String)
    fun datePeriodReachedLast()
    fun datePeriodReachedFirst()
}
