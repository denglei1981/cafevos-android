package com.changanford.circle.adapter.circle

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.bean.MyJoinCircleBean
import com.changanford.circle.databinding.ItemHoldCircleBinding
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toIntPx

/**
 * @author: niubobo
 * @date: 2024/6/4
 * @description：
 */
class HoldCircleAdapter :
    BaseQuickAdapter<MyJoinCircleBean, BaseDataBindingHolder<ItemHoldCircleBinding>>(
        R.layout.item_hold_circle
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemHoldCircleBinding>,
        item: MyJoinCircleBean
    ) {
        holder.dataBinding?.apply {
            val loadRes = if (item.isCheck) R.mipmap.shop_order_cb_1 else R.mipmap.shop_order_cb_0
            tvCircle.text = if (item.circleLord == true) " (圈主)" else ""
            llContent.post {
                val tvContentMaxWidth =
                    llContent.width - 28.toIntPx() - ivType.width - ivIcon.width - tvCircle.width
                tvContent.maxWidth = tvContentMaxWidth
                tvContent.text = item.name
            }
            ivType.setImageResource(loadRes)
            GlideUtils.loadCircle(item.pic, ivIcon)
        }
    }
}