package com.changanford.circle.adapter

import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleNoticeBinding
import com.changanford.circle.ext.loadCircleImage
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.utils.MUtils.expandText
import com.changanford.circle.utils.MUtils.setTopMargin
import com.changanford.common.bean.TestBean
import com.changanford.common.constant.TestImageUrl


/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose
 */
class CircleNoticeAdapter :
    BaseQuickAdapter<TestBean, BaseDataBindingHolder<ItemCircleNoticeBinding>>(
        R.layout.item_circle_notice
    ) {
    override fun convert(holder: BaseDataBindingHolder<ItemCircleNoticeBinding>, item: TestBean) {
        holder.dataBinding?.run {
            setTopMargin(this.root, 19, holder.layoutPosition)
            ivHead.loadCircleImage(TestImageUrl)
            tvContent.text = item.testString
            tvContent.post {
                expandText(tvContent, item.testString)
            }

        }
    }

}