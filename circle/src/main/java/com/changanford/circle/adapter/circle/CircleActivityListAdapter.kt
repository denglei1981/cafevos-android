package com.changanford.circle.adapter.circle

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleDetailsPersonalAdapter
import com.changanford.circle.bean.User
import com.changanford.circle.databinding.ItemCircleActivityListBinding
import com.changanford.circle.ext.loadColLImage
import com.changanford.circle.ext.setCircular
import com.changanford.circle.utils.MUtils
import com.changanford.common.bean.TestBean
import com.changanford.common.constant.TestImageUrl

/**
 *Author lcw
 *Time on 2022/8/18
 *Purpose
 */
class CircleActivityListAdapter :
    BaseQuickAdapter<TestBean, BaseDataBindingHolder<ItemCircleActivityListBinding>>(
        R.layout.item_circle_activity_list
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleActivityListBinding>,
        item: TestBean
    ) {
        val personalAdapter = CircleDetailsPersonalAdapter(context)
        personalAdapter.setItems(
            arrayListOf(
                User(avatar = TestImageUrl),
                User(avatar = TestImageUrl),
                User(avatar = TestImageUrl),
                User(avatar = TestImageUrl),
            )
        )
        holder.dataBinding?.apply {
            MUtils.setTopMargin(this.root, 12, holder.layoutPosition)
            ivCover.setCircular(5)
            ivCover.loadColLImage(TestImageUrl)
            ryHead.adapter = personalAdapter
            if (holder.layoutPosition == 1) {
                btnType.setBackgroundResource(R.drawable.bg_f2f4_14)
                btnType.setTextColor(ContextCompat.getColor(context, R.color.color_00095B))
            } else {
                btnType.setBackgroundResource(R.drawable.bg_dd_14)
                btnType.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }
}