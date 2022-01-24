package com.changanford.home.recommend.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.data.AdBean
import com.changanford.home.databinding.ItemHomeRecommendFastInBinding

// 不确定什么布局
class RecommendFastInListAdapter :
    BaseQuickAdapter<AdBean, BaseDataBindingHolder<ItemHomeRecommendFastInBinding>>(R.layout.item_home_recommend_fast_in) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeRecommendFastInBinding>,
        item: AdBean
    ) {
        holder.dataBinding?.let {
            GlideUtils.loadBD(item.adImg, it.ivOne)

            it.ivOne.setOnClickListener {
                JumpUtils.instans?.jump(item.jumpDataType,item.jumpDataValue)
            }
            try{
                it.tvAdName.text=item.adSubName
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }
}