package com.changanford.home.news.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.AdBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.databinding.ItemNewsDetailsAdsBinding

class NewsAdsListAdapter :
    BaseQuickAdapter<AdBean, BaseDataBindingHolder<ItemNewsDetailsAdsBinding>>(R.layout.item_news_details_ads) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemNewsDetailsAdsBinding>,
        item: AdBean
    ) {
        holder.dataBinding?.let {
            GlideUtils.loadBD(item.adImg, it.ivPic)
            it.ivPic.setOnClickListener {
                JumpUtils.instans?.jump(item.jumpDataType,item.jumpDataValue)
            }
        }
    }
}