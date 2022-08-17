package com.changanford.circle.adapter.circle

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleDetailsActivityBinding
import com.changanford.circle.ext.loadColLImage
import com.changanford.circle.ext.setCircular
import com.changanford.common.bean.TestBean
import com.changanford.common.constant.TestImageUrl

/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose
 */
class CircleDetailsActivityAdapter :
    BaseQuickAdapter<TestBean, BaseDataBindingHolder<ItemCircleDetailsActivityBinding>>(
        R.layout.item_circle_details_activity
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleDetailsActivityBinding>,
        item: TestBean
    ) {
        holder.dataBinding?.apply {
            ivBg.setCircular(5)
            ivBg.loadColLImage(TestImageUrl)
            tvContent.text=item.testString
        }
    }
}