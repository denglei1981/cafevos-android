package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.ShopRecommendBean
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemHomeRecommendList1Binding
import com.changanford.shop.ui.goods.RecommendActivity

class ShopRecommendListAdapter1: BaseQuickAdapter<ShopRecommendBean, BaseDataBindingHolder<ItemHomeRecommendList1Binding>>(
    R.layout.item_home_recommend_list1){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemHomeRecommendList1Binding>, item: ShopRecommendBean) {
        holder.dataBinding?.apply {
            tvName.setText(item.kindName)
            recyclerView.adapter=ShopRecommendListAdapter2().apply {
                setList(item.spuInfoList)
            }
            tvMore.setOnClickListener {
                RecommendActivity.start(item.kindId)
            }
        }
    }
}