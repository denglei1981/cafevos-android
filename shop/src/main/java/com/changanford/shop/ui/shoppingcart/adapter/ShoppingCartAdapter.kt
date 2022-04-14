package com.changanford.shop.ui.shoppingcart.adapter

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.MyApp
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.shop.R
import com.changanford.shop.api.ShopNetWorkApi
import com.changanford.shop.databinding.ItemShoppingCartBinding
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.utils.launchWithCatch

/**
 *   购物车
 * */
class ShoppingCartAdapter(
    val lifecycleOwner11: LifecycleOwner,
    val shopBackListener: ShopBackListener
) :
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
            val limitBuyNum: Int = item.getCurrentLimitBuyNum()

            val nowStock = item.stock

            var isLimitBuyNum = false//是否限购
            val max: Int = if (limitBuyNum in 1..nowStock) {
                isLimitBuyNum = true
                limitBuyNum
            } else nowStock
            addSubtractView.setMax(max, isLimitBuyNum)

            item.num?.let { n ->
                addSubtractView.setNumber(n, false)
                if (n > 1) {
                    addSubtractView.setReduceAddGrayOrBlack(true)
                } else {
                    addSubtractView.setReduceAddGrayOrBlack(false)
                }
            }
            addSubtractView.setEditBlean(false)
            imgGoodsCover.setOnClickListener {

                GoodsDetailsActivity.start(item.mallMallSpuId)

            }

            addSubtractView.numberLiveData.observe(lifecycleOwner11, Observer {
                //数量变化
                var isContains = false
                item.num = it
                item.num?.let { // 通知接口 购物车数量变化了。
                    lifecycleOwner11.launchWithCatch {
                        val body = MyApp.mContext.createHashMap()
                        body["mallMallUserSkuId"] = item.mallMallUserSkuId
                        body["num"] = item.num!!
                        val rKey = getRandomKey()
                        ApiClient.createApi<ShopNetWorkApi>()
                            .userSkuNumChange(body.header(rKey), body.body(rKey)).also { cr ->
                                item.num?.let { n ->
                                    if (n > 1) {
                                        addSubtractView.setReduceAddGrayOrBlack(true)
                                    } else {
                                        addSubtractView.setReduceAddGrayOrBlack(false)
                                    }
                                    if (n >= item.stock) { // 库存不足
                                        addSubtractView.setAddGrayOrBlack(false)
                                    } else {
                                        addSubtractView.setAddGrayOrBlack(true)
                                    }

                                }

                            }
                    }
                }

                if (shopList.contains(item)) {
                    shopList.remove(item)
                    isContains = true
                }

                if (isContains) {
                    shopList.add(item)
                    shopBackListener.check()
                }
            })

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

