package com.ban.daterangeselectionbar.lib.util

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

fun RecyclerView.attachSnapHelperWithListener(snapHelper: SnapHelper, onSnapPositionChangeListener: DRSSnapOnScrollListener.DRSOnSnapPositionChangeListener) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener =
        DRSSnapOnScrollListener(
            snapHelper,
            onSnapPositionChangeListener
        )
    addOnScrollListener(snapOnScrollListener)
}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}

class DRSSnapOnScrollListener(
        private val snapHelper: SnapHelper,
        private val onSnapPositionChangeListener: DRSOnSnapPositionChangeListener
) : RecyclerView.OnScrollListener() {

    interface DRSOnSnapPositionChangeListener {
        fun onSnapPositionChange(position: Int)
    }

    private var lastSnapPosition: Int = RecyclerView.NO_POSITION

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            notifySnapPositionChange(recyclerView)
        }
    }

    private fun notifySnapPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)
        val snapPositionChanged = lastSnapPosition != snapPosition
        if (snapPositionChanged) {
            onSnapPositionChangeListener.onSnapPositionChange(snapPosition)
            lastSnapPosition = snapPosition
        }
    }
}
