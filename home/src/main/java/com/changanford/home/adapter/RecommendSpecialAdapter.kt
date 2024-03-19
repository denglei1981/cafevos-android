package com.changanford.home.adapter

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.SpecialListBean
import com.changanford.common.util.MConstant
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.common.utilext.toIntPx
import com.changanford.home.R
import com.changanford.home.databinding.ItemRecommendHomeSpecialChiledBinding

/**
 * @author: niubobo
 * @date: 2024/2/19
 * @description：
 */
class RecommendSpecialAdapter :
    BaseQuickAdapter<SpecialListBean, BaseDataBindingHolder<ItemRecommendHomeSpecialChiledBinding>>(
        R.layout.item_recommend_home_special_chiled
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemRecommendHomeSpecialChiledBinding>,
        item: SpecialListBean
    ) {
        holder.dataBinding?.apply {
            setMargin(clContent,28,holder.layoutPosition)
            clContent.maxWidth = ((204.toDouble() / 375) * MConstant.deviceWidth).toInt()
            ivIcon.setCircular(12)
            ivIcon.loadCompress(item.getPicUrl())
            tvTitle.text = item.title
            tvContent.text = item.summary
        }
    }

    private fun setMargin(view: View, margin: Int, position: Int) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0 || position == 1 ) {
            params.leftMargin = 0
        } else params.leftMargin = margin.toIntPx()
    }
}