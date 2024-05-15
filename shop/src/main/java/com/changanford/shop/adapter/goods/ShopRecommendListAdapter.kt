package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GioPreBean
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.MUtils
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.utilext.load
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemShopRecommendListBinding

class ShopRecommendListAdapter :
    BaseQuickAdapter<GoodsItemBean, BaseDataBindingHolder<ItemShopRecommendListBinding>>(
        R.layout.item_shop_recommend_list
    ) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemShopRecommendListBinding>,
        item: GoodsItemBean
    ) {
        holder.dataBinding?.apply {
            MUtils.setTopMargin(root, 15, holder.layoutPosition)
            tvPosition.text = (holder.layoutPosition + 1).toString()
            ivCover.setCircular(12)
            ivCover.load(item.getImgPath())
            tvContent.text = item.spuName
            tvPrice.text = "¥${item.getRMB(item.priceFb)}"
            tvChangeNum.text = "已兑换${item.salesCount}件"
            root.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("spuId", item.spuId)
                bundle.putParcelable(
                    GioPageConstant.shopPreBean,
                    GioPreBean("推荐榜单页", "推荐榜单页")
                )
                startARouter(ARouterShopPath.ShopGoodsActivity, bundle)
            }
        }
    }
}