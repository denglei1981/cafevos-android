package com.changanford.home.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.util.MConstant
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.home.R
import com.changanford.home.bean.SpecialListBean
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
            clContent.maxWidth = ((204.toDouble() / 375) * MConstant.deviceWidth).toInt()
            ivIcon.setCircular(12)
            ivIcon.loadCompress(item.getPicUrl())
            tvTitle.text = item.title
            tvContent.text = item.summary
        }
    }
}