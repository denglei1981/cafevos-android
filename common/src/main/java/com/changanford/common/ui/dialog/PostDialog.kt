package com.changanford.common.ui.dialog

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.changanford.common.R
import com.luck.picture.lib.tools.ScreenUtils


class PostDialog(context: Context,
private val title:String,
private val postButtonListener: PostButtonListener
) : BaseDialog(context) {

    private var tvtitle: TextView? = null
    private var tvcancle: TextView? = null
    private var tvsave: TextView? = null
    override fun getLayoutId(): Int {
        return R.layout.postdialog
    }

    init {
        window?.setGravity(Gravity.CENTER)
        window?.attributes?.width = ScreenUtils.getScreenWidth(context)*5/6
        initview()
    }

    fun initview() {
        tvtitle = findViewById(R.id.tv_title)
        tvcancle = findViewById(R.id.tv_cancle)
        tvsave = findViewById(R.id.tv_save)
        tvtitle?.text = "$title"
        tvcancle?.setOnClickListener {
            postButtonListener.cancle()
            dismiss()
        }
        tvsave?.setOnClickListener {
            postButtonListener.save()
            dismiss()
        }
    }

    interface PostButtonListener {
        fun save()
        fun cancle()
    }
}