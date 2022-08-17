package com.changanford.circle.adapter.circle

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleDetailsTopicBinding
import com.changanford.common.bean.TestBean

/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose
 */
class CircleDetailsTopicAdapter :
    BaseQuickAdapter<TestBean, BaseDataBindingHolder<ItemCircleDetailsTopicBinding>>(
        R.layout.item_circle_details_topic
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleDetailsTopicBinding>,
        item: TestBean
    ) {
        holder.dataBinding?.apply {
            tvContent.text = item.testString
        }
    }
}