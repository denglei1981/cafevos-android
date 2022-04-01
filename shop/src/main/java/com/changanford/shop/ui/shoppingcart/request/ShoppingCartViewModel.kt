package com.changanford.shop.ui.shoppingcart.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.shop.api.ShopNetWorkApi


class ShoppingCartViewModel : BaseViewModel() {

    var  goodsList: MutableLiveData<MutableList<GoodsItemBean>> = MutableLiveData()

    fun getShoppingCartList(){
        launch (block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<ShopNetWorkApi>().getShoppingCartList(body.header(rKey),body.body(rKey))
                .onSuccess {
                    goodsList.postValue(it)
                }
                .onWithMsgFailure {
                    it?.toast()

                }
        })
    }
}