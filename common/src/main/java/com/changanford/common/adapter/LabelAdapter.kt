package com.changanford.common.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.R
import com.changanford.common.bean.Imag
import com.changanford.common.util.DisplayUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils.loadBD


/**
 * 用户头像旁边标签adapter isbig
 *  isShow： 是否显示判断
 */
class LabelAdapter(var size: Int, var isShow: Boolean = true) :
    BaseQuickAdapter<Imag, BaseViewHolder>(R.layout.item_user_lable_img) {
    override fun convert(
        baseViewHolder: BaseViewHolder,
        labelBean: Imag
    ) {
        val lable_icon =
            baseViewHolder.itemView.findViewById<ImageView>(R.id.label_icon)
        lable_icon.setOnClickListener {
            JumpUtils.instans?.jump(labelBean.jumpDataType, labelBean.jumpDataValue)
        }
        val params = lable_icon.layoutParams
        params.width =
            DisplayUtil.dip2px(lable_icon.context, size.toFloat())
        params.height =
            DisplayUtil.dip2px(lable_icon.context, size.toFloat())
        lable_icon.layoutParams = params
        loadBD(labelBean.img, lable_icon)

        lable_icon.setOnClickListener {
             JumpUtils.instans?.jump(labelBean.jumpDataType,labelBean.jumpDataValue)
        }

    }

    override fun addData(newData: Collection<Imag>) {
        if (isShow) {
            super.addData(newData)
        }
    }


}