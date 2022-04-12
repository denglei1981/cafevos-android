package com.changanford.shop.ui.shoppingcart.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.shop.api.ShopNetWorkApi


class ShoppingCartViewModel : BaseViewModel() {

    var goodsListLiveData: MutableLiveData<MutableList<GoodsDetailBean>> = MutableLiveData()
    var goodsInvaildListLiveData: MutableLiveData<MutableList<GoodsDetailBean>> = MutableLiveData() // 失效商品
    var deleteShoppingCar: MutableLiveData<String> = MutableLiveData()

    fun getShoppingCartList() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<ShopNetWorkApi>()
                .getShoppingCartList(body.header(rKey), body.body(rKey))
                .onSuccess {
                    if (it != null) {
                        showGoodList(it)
                    }
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }

    fun showGoodList(goodList: MutableList<GoodsDetailBean>) {
        val canBuyGoodList: MutableList<GoodsDetailBean> = mutableListOf()
        val invaildGoodList: MutableList<GoodsDetailBean> = mutableListOf()
        goodList.forEach {
            if (it.mallSkuState == "0" && it.stock ==0) {
                invaildGoodList.add(it)
            } else {
                canBuyGoodList.add(it)
            }
        }
        goodsListLiveData.postValue(canBuyGoodList)
        goodsInvaildListLiveData.postValue(invaildGoodList)
    }

    fun deleteCartShopping(mallUserSkuIds: ArrayList<String>) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["mallUserSkuIds"] = mallUserSkuIds
            ApiClient.createApi<ShopNetWorkApi>()
                .deleteShoppingCart(body.header(rKey), body.body(rKey))
                .onSuccess {
                    // 删除成功, 重新请求 购物车数据
                    getShoppingCartList()
                    deleteShoppingCar.postValue("成功")
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }
}