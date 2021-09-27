package com.changanford.circle.adapter

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.changanford.circle.R
import com.changanford.common.util.PictureUtil
import com.changanford.common.utilext.GlideUtils
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.DoubleUtils
import com.yalantis.ucrop.UCrop
import com.zhpan.bannerview.BaseViewHolder


class PictureAdapterViewHolder(itemView:View,activity: Activity,type:Int) : BaseViewHolder<LocalMedia>(itemView) {
    var pic: ImageView? = findView(R.id.pic)
    var etcontent: EditText? = findView(R.id.et_picmiaoshu)
    var tv_tiaozhen: TextView? = findView(R.id.tv_tiaozhen)
    var activity: Activity = activity
    var showedittype =type
    override fun bindData(data: LocalMedia?, position: Int, pageSize: Int) {
        if (showedittype==-1)etcontent?.visibility =View.GONE else etcontent?.visibility = View.VISIBLE
        pic?.let { GlideUtils.loadBD(data?.let { PictureUtil.getFinallyPath(it) }, it) }
        etcontent?.setText(data?.contentDesc)
        tv_tiaozhen?.setOnClickListener {
            if (!DoubleUtils.isFastDoubleClick()) {
                data?.let { it1 -> PictureUtil.getFinallyPath(it1) }?.let { it2 ->
                    PictureUtil.startUCrop(
                        activity,
                        it2,
                        UCrop.REQUEST_CROP,
                        16f,
                        9f
                    )
                }
            }
        }

        etcontent!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                data?.contentDesc = s.toString()
            }
        })


    }
}