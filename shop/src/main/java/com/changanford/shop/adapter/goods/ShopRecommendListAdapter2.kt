package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CirCleHotList
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.utilext.load
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemHomeRecommendList1Binding
import com.changanford.shop.databinding.ItemHomeRecommendList2Binding
import com.changanford.shop.ui.goods.GoodsDetailsActivity

class ShopRecommendListAdapter2: BaseQuickAdapter<GoodsItemBean, BaseDataBindingHolder<ItemHomeRecommendList2Binding>>(
    R.layout.item_home_recommend_list2){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemHomeRecommendList2Binding>, item: GoodsItemBean) {
        holder.dataBinding?.apply {
            imgCover.load(item.getImgPath())
            tvSales.setText("${item.salesCount}")
            model=item
            executePendingBindings()
            root.setOnClickListener {
                GoodsDetailsActivity.start(item.spuId)
            }
        }
    }
}