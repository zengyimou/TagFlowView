package com.mib.tagflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
	private lateinit var tfl: TagFlowLayout

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		tfl = findViewById(R.id.tfl)
		initTagView()
	}

	private fun getList(): List<TagSelectItem>{
		val list = mutableListOf(
			TagSelectItem().apply {
				key = "1"
				value = "test1"
			},
			TagSelectItem().apply {
				key = "2"
				value = "test2"
			},
			TagSelectItem().apply {
				key = "3"
				value = "test3"
			},
		)
		return list
	}

	private fun initTagView() {
		setAdapter(tfl = tfl, list = getList())
		setListener()
	}

	private fun setAdapter(tfl: TagFlowLayout, list: List<TagSelectItem>) {
		tfl.setAdapter(object : TagAdapter<TagSelectItem>(list) {
			override fun getView(
				parent: FlowLayout?,
				position: Int,
				t: TagSelectItem?
			): View {
				return createNewView(parent, t?.value ?: "")
			}
		})
	}

	private fun setListener(){
		tfl.setOnSelectListener(object: TagFlowLayout.OnSelectListener{
			override fun onSelected(selectPosSet: Set<Int>?) {
			}

		})
		tfl.setOnTagClickListener(object : TagFlowLayout.OnTagClickListener {
			override fun onTagClick(view: View?, position: Int, parent: FlowLayout?): Boolean {
				return false
			}

		})
	}

	/**
	 * 初始化标签
	 * @param parent FlowLayout?
	 * @param text String
	 * @return View
	 */
	fun createNewView(parent: FlowLayout?, text: String): View {
		val tv: TextView = LayoutInflater.from(this).inflate(
			R.layout.item_tag,
			parent, false
		) as TextView
		tv.text = text
		return tv
	}
}

