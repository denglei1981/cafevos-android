package com.changanford.shop.ui.shoppingcart.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemShoppingCartBinding

/**
 *   购物车
 * */
class ShoppingCartAdapter(val shopBackListener: ShopBackListener) :
    BaseQuickAdapter<GoodsDetailBean, BaseDataBindingHolder<ItemShoppingCartBinding>>(R.layout.item_shopping_cart),
    LoadMoreModule {

    val checkMap: HashMap<Long, Boolean> = HashMap()
    val shopList: MutableList<GoodsDetailBean> = mutableListOf() // 选中的商品

    override fun convert(
        holder: BaseDataBindingHolder<ItemShoppingCartBinding>,
        item: GoodsDetailBean
    ) {
        holder.dataBinding?.apply {
            model = item
            GlideUtils.loadBD(item.skuImg, imgGoodsCover)
            tvIntegral.setText("￥" + item.getRMB(item.fbPer))
            checkStatus.setOnCheckedChangeListener { _, isChecked ->
                checkMap[item.mallMallUserSkuId] = isChecked
                if (isChecked) {
                    if (!shopList.contains(item)) {
                        shopList.add(item)
                    }
                } else {
                    if (shopList.contains(item)) {
                        shopList.remove(item)
                    }
                }
                shopBackListener.check()
            }
            checkStatus.isChecked = checkMap[item.mallMallUserSkuId]!!
            val goodsAttributeAdapter = GoodsAttributeAdapter()
            goodsAttributeAdapter.setList(item.getTagList())
            rvGoodsProperty.adapter = goodsAttributeAdapter


        }
    }

    fun getShoppingList() {
        checkMap.forEach { (k, v) ->


        }
    }

    interface ShopBackListener {

        fun check()
    }
}

