package com.changanford.shop.ui.shoppingcart.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemInvaildShoppingCartBinding
import com.changanford.shop.databinding.ItemShoppingCartBinding

/**
 *    失效的商品 ，购物车
 * */
class ShoppingCartInvaildAdapter(val shopBackListener: ShopBackListener) :
    BaseQuickAdapter<GoodsDetailBean, BaseDataBindingHolder<ItemInvaildShoppingCartBinding>>(R.layout.item_invaild_shopping_cart),
    LoadMoreModule {



    init {
        addChildClickViewIds(R.id.iv_delete)
    }

    override fun convert(
        holder: BaseDataBindingHolder<ItemInvaildShoppingCartBinding>,
        item: GoodsDetailBean
    ) {
        holder.dataBinding?.apply {
            model = item
            GlideUtils.loadBD(item.skuImg, imgGoodsCover)
            val goodsAttributeAdapter = GoodsAttributeGrayAdapter()
            goodsAttributeAdapter.setList(item.getTagList())
            rvGoodsProperty.adapter = goodsAttributeAdapter
        }
    }


    interface ShopBackListener {

        fun check()
    }
}

