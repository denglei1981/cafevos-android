package com.changanford.common.ui.dialog

import android.content.Context
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.R
import com.changanford.common.adapter.FordPaiBottomDialogAdapter
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey


/**
 * @Author: lw
 * @Date: 2024/2/26
 * @Des:福特新底部样式
 */
class FordPaiBottomDialog(
    context: Context,
    title: String,
    list: ArrayList<String>,
    lifecycle: LifecycleOwner,
    listener: OnItemClickListener
) : BaseDialog(context) {

    override fun getLayoutId() = R.layout.ford_pai_bottom_dialog

    private val adapter by lazy { FordPaiBottomDialogAdapter() }

    init {
        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
        findViewById<AppCompatTextView>(R.id.tv_cancel).setOnClickListener {
            dismiss()
        }
        findViewById<AppCompatTextView>(R.id.tv_title).text = title
        adapter.setList(list)
        val ry = findViewById<RecyclerView>(R.id.ry)
        ry.adapter = adapter
        adapter.setOnItemClickListener(listener)
        LiveDataBus.get().with(LiveDataBusKey.DISMISS_FORD_PAI_DIALOG).observe(lifecycle){
            dismiss()
        }
    }

}