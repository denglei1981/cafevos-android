package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.ShopRecommendBean
import com.changanford.common.util.gio.GIOUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemHomeRecommendList1Binding
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.ui.goods.RecommendActivity

class ShopRecommendListAdapter1 :
    BaseQuickAdapter<ShopRecommendBean, BaseDataBindingHolder<ItemHomeRecommendList1Binding>>(
        R.layout.item_home_recommend_list1
    ) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeRecommendList1Binding>,
        item: ShopRecommendBean
    ) {
        holder.dataBinding?.apply {
            tvName.setText(item.kindName)

            val shopAdapter = ShopRecommendListAdapter2().apply {
                setList(item.spuInfoList)
            }
            shopAdapter.setOnItemClickListener { adapter, view, position ->
                val shopItem = shopAdapter.getItem(position)
                GoodsDetailsActivity.start(shopItem.spuId)
                GIOUtils.homePageClick(
                    "推荐榜单",
                    (position + 1).toString(),
                    "${item.kindName}-${shopItem.spuName}"
                )
            }
            recyclerView.adapter = shopAdapter
            tvMore.setOnClickListener {
                GIOUtils.homePageClick("推荐榜单", 0.toString(), item.kindName)
                RecommendActivity.start(item.kindName)
            }
        }
    }
}