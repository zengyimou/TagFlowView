package com.mib.tagflow

import android.view.View

/**
 *  author : cengyimou
 *  date : 2023/8/11 17:25
 *  description :
 */
abstract class TagAdapter<T>(datas: List<T>?) {
    private var mTagDatas: List<T>? = datas
    private var mOnDataChangedListener: OnDataChangedListener? = null
    private val mCheckedPosList = mutableSetOf<Int>()

    interface OnDataChangedListener {
        fun onChanged()
    }

    fun setOnDataChangedListener(listener: OnDataChangedListener?) {
        mOnDataChangedListener = listener
    }

    fun setSelectedList(vararg poses: Int) {
        val set: MutableSet<Int> = HashSet()
        for (pos in poses) {
            set.add(pos)
        }
        setSelectedList(set)
    }

    fun setSelectedList(set: Set<Int>?) {
        mCheckedPosList.clear()
        if (set != null) {
            mCheckedPosList.addAll(set)
        }
        notifyDataChanged()
    }

    fun getPreCheckedList(): MutableSet<Int> {
        return mCheckedPosList
    }

    fun getCount(): Int {
        return if (mTagDatas == null) 0 else mTagDatas!!.size
    }

    private fun notifyDataChanged() {
        if (mOnDataChangedListener != null) mOnDataChangedListener!!.onChanged()
    }

    fun getItem(position: Int): T? {
        return mTagDatas?.getOrNull(position)
    }

    abstract fun getView(parent: FlowLayout?, position: Int, t: T?): View

}