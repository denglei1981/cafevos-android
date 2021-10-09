package com.changanford.common.widget

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.R
import com.changanford.common.ui.dialog.BaseDialog

/**
 * @Author: hpb
 * @Date: 2020/5/13
 * @Des:通用底部dialog
 */
open class HomeBottomDialog(context: Context, vararg strs: String) : BaseDialog(context) {

    private val adapter = MyAdapter()
    private var clickItemListener: OnClickItemListener? = null

    override fun getLayoutId(): Int {
        return R.layout.base_dialog_bottom
    }

    init {
        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
        findViewById<TextView>(R.id.cancel_tv).setOnClickListener { dismiss() }
        findViewById<RecyclerView>(R.id.recycler_view).adapter = adapter
        adapter.setOnItemClickListener { _, _, position ->
            clickItemListener?.onClickItem(position, adapter.getItem(position))
            dismiss()
        }
        adapter.setList(strs.asList())
    }

    inner class MyAdapter :
        BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_dialog_home_bottom) {

        override fun convert(holder: BaseViewHolder, item: String) {
            holder.setText(R.id.content_tv, item)
        }

    }

    fun setOnClickItemListener(clickItemListener: OnClickItemListener): HomeBottomDialog {
        this.clickItemListener = clickItemListener
        return this
    }

    interface OnClickItemListener {
        fun onClickItem(position: Int, str: String)
    }

}