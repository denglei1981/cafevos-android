package com.changanford.home.news.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.util.ext.loadCircleImage
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.databinding.ItemHomeNewsRecommendBinding

class NewsRecommendListAdapter :
    BaseQuickAdapter<InfoDataBean, BaseDataBindingHolder<ItemHomeNewsRecommendBinding>>(R.layout.item_home_news_recommend) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeNewsRecommendBinding>,
        item: InfoDataBean
    ) {

        holder.dataBinding?.let {
            it.ivHead.loadCircleImage(item.authors?.avatar)
            it.tvName.text = item.authors?.nickname
            it.tvHomeTitle.text = item.title
            GlideUtils.loadBD(item.getPicCover(), it.ivHomeNews)
        }


    }
}