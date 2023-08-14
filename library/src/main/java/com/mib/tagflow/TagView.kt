package com.mib.tagflow

import android.R
import android.content.Context
import android.view.View
import android.widget.Checkable
import android.widget.FrameLayout

/**
 *  author : cengyimou
 *  date : 2023/8/11 17:24
 *  description :
 */
class TagView(context: Context) : FrameLayout(context), Checkable {
    private var isChecked = false
    fun getTagView(): View {
        return getChildAt(0)
    }

    public override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val states = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked()) {
            mergeDrawableStates(states, CHECK_STATE)
        }
        return states
    }

    /**
     * Change the checked state of the view
     *
     * @param checked The new checked state
     */
    override fun setChecked(checked: Boolean) {
        if (isChecked != checked) {
            isChecked = checked
            refreshDrawableState()
        }
    }

    /**
     * @return The current checked state of the view
     */
    override fun isChecked(): Boolean {
        return isChecked
    }

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    override fun toggle() {
        setChecked(!isChecked)
    }

    companion object {
        private val CHECK_STATE = intArrayOf(R.attr.state_checked)
    }
}