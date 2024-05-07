package com.changanford.circle.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleTopRecommendBinding
import com.changanford.circle.ui.activity.CircleListActivity
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.ext.loadImage
import com.changanford.common.util.gio.GIOUtils

/**
 * @author: niubobo
 * @date: 2024/3/13
 * @description：
 */
class CircleTopRecommendAdapter :
    BaseQuickAdapter<NewCircleBean, BaseDataBindingHolder<ItemCircleTopRecommendBinding>>(
        R.layout.item_circle_top_recommend
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleTopRecommendBinding>,
        item: NewCircleBean
    ) {
        holder.dataBinding?.run {
//            ivCover.setCircular(12)
            ivCover.loadImage(item.icon)
            tvName.text=item.name
            root.setOnClickListener {
                WBuriedUtil.clickCircleTop(item.name)
                CircleListActivity.start(item.id)
                var position = 0
                data.forEachIndexed { index, newCircleBean ->
                    if (newCircleBean.id == item.id) {
                        position = index
                    }
                }
                GIOUtils.homePageClick(
                    "圈子分类区",
                    (position + 1).toString(),
                    item.name
                )
            }
        }
    }
}