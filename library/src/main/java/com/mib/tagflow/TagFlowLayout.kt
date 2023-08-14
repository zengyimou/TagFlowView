package com.mib.tagflow

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener

/**
 *  author : cengyimou
 *  date : 2023/8/11 17:27
 *  description :
 */
class TagFlowLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    FlowLayout(context, attrs, defStyle), TagAdapter.OnDataChangedListener {
    private var mTagAdapter: TagAdapter<TagSelectItem>? = null
    private var mSelectedMax = -1 //-1为不限制数量
    private val mSelectedSet: MutableSet<Int> = mutableSetOf()
    private var mOnSelectListener: OnSelectListener? = null
    private var mOnTagClickListener: OnTagClickListener? = null


    interface OnSelectListener {
        fun onSelected(selectPosSet: Set<Int>?)
    }

    interface OnTagClickListener {
        fun onTagClick(view: View?, position: Int, parent: FlowLayout?): Boolean
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val cCount = childCount
        for (i in 0 until cCount) {
            val tagView = getChildAt(i) as TagView
            if (tagView.visibility == GONE) {
                continue
            }
            if (tagView.getTagView().visibility == GONE) {
                tagView.visibility = GONE
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setOnSelectListener(onSelectListener: OnSelectListener?) {
        mOnSelectListener = onSelectListener
    }

    fun setOnTagClickListener(onTagClickListener: OnTagClickListener?) {
        mOnTagClickListener = onTagClickListener
    }

    fun setAdapter(adapter: TagAdapter<TagSelectItem>) {
        mTagAdapter = adapter
        mTagAdapter?.setOnDataChangedListener(this)
        mSelectedSet.clear()
        changeAdapter()
    }

    private fun changeAdapter() {
        removeAllViews()
        val adapter = mTagAdapter
        var tagViewContainer: TagView?
        val preCheckedList: MutableSet<Int>? = mTagAdapter?.getPreCheckedList()
        val count = adapter?.getCount()?: 0
        for (i in 0 until count) {
            val tagView = adapter?.getView(this, i, adapter.getItem(i))
            tagViewContainer = TagView(context)
            tagView?.isDuplicateParentStateEnabled = true
            if (tagView?.layoutParams != null) {
                tagViewContainer.layoutParams = tagView.layoutParams
            } else {
                val lp = MarginLayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(
                    dip2px(context, 5f),
                    dip2px(context, 5f),
                    dip2px(context, 5f),
                    dip2px(context, 5f)
                )
                tagViewContainer.layoutParams = lp
            }
            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            tagView?.layoutParams = lp
            tagViewContainer.addView(tagView)
            addView(tagViewContainer)
            if (preCheckedList?.contains(i) == true) {
                setChildChecked(tagViewContainer)
            }
            tagView?.isClickable = false
            val finalTagViewContainer: TagView = tagViewContainer
            tagViewContainer.setOnClickListener(OnClickListener {
                doSelect(finalTagViewContainer, i)
                if (mOnTagClickListener != null) {
                    mOnTagClickListener!!.onTagClick(
                        finalTagViewContainer, i,
                        this@TagFlowLayout
                    )
                }
            })
        }
        preCheckedList?.let { mutableSet ->
            mSelectedSet.addAll(mutableSet)
        }
    }

    fun setMaxSelectCount(count: Int) {
        if (mSelectedSet.size > count) {
            mSelectedSet.clear()
        }
        mSelectedMax = count
    }

    fun getSelectedList(): Set<Int> {
        return HashSet(mSelectedSet)
    }

    private fun setChildChecked(view: TagView) {
        view.isChecked = true
    }

    private fun setChildUnChecked(view: TagView) {
        view.isChecked = false
    }

    private fun doSelect(child: TagView, position: Int) {
        if (!child.isChecked) {
            //处理max_select=1的情况
            if (mSelectedMax == 1 && mSelectedSet.size == 1) {
                val iterator: Iterator<Int> = mSelectedSet.iterator()
                val preIndex = iterator.next()
                val pre = getChildAt(preIndex) as TagView
                setChildUnChecked(pre)
                setChildChecked(child)
                mSelectedSet.remove(preIndex)
                mSelectedSet.add(position)
            } else {
                if (mSelectedMax > 0 && mSelectedSet.size >= mSelectedMax) {
                    return
                }
                setChildChecked(child)
                mSelectedSet.add(position)
            }
        } else {
            setChildUnChecked(child)
            mSelectedSet.remove(position)
        }
        if (mOnSelectListener != null) {
            mOnSelectListener!!.onSelected(HashSet(mSelectedSet))
        }
    }

    fun getAdapter(): TagAdapter<*>? {
        return mTagAdapter
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.flow_layout)
        mSelectedMax = ta.getInt(R.styleable.flow_layout_max_select, -1)
        ta.recycle()
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState())
        var selectPos = ""
        if (mSelectedSet.size > 0) {
            for (key in mSelectedSet) {
                selectPos += "$key|"
            }
            selectPos = selectPos.substring(0, selectPos.length - 1)
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val mSelectPos = state.getString(KEY_CHOOSE_POS)
            if (!TextUtils.isEmpty(mSelectPos)) {
                val split = mSelectPos!!.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                for (pos in split) {
                    val index = pos.toInt()
                    mSelectedSet.add(index)
                    val tagView = getChildAt(index) as TagView
                    setChildChecked(tagView)
                }
            }
            super.onRestoreInstanceState(state.getParcelable(KEY_DEFAULT))
            return
        }
        super.onRestoreInstanceState(state)
    }

    override fun onChanged() {
        mSelectedSet.clear()
        changeAdapter()
    }

    companion object {
        private const val TAG = "TagFlowLayout"
        private const val KEY_CHOOSE_POS = "key_choose_pos"
        private const val KEY_DEFAULT = "key_default"
        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }
}