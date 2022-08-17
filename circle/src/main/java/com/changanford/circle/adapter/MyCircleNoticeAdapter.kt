package com.changanford.circle.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemMyCircleNoticeBinding
import com.changanford.circle.utils.MUtils
import com.changanford.common.bean.TestBean

/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose
 */
class MyCircleNoticeAdapter :
    BaseQuickAdapter<TestBean, BaseDataBindingHolder<ItemMyCircleNoticeBinding>>(
        R.layout.item_my_circle_notice
    ) {
    override fun convert(holder: BaseDataBindingHolder<ItemMyCircleNoticeBinding>, item: TestBean) {
        holder.dataBinding?.run {
            MUtils.setTopMargin(this.root, 19, holder.layoutPosition)
            tvContent.text = item.testString
            tvContent.post {
                MUtils.expandText(tvContent, item.testString)
            }
            if (holder.layoutPosition == itemCount - 1) {
                llReason.visibility = View.VISIBLE
            } else {
                llReason.visibility = View.GONE
            }
        }
    }
}