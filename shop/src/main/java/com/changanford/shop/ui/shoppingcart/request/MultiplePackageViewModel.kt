package com.changanford.shop.ui.shoppingcart.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.shop.api.ShopNetWorkApi
import com.changanford.shop.bean.PackMainData

class MultiplePackageViewModel: BaseViewModel() {

    var packMainDataLiveData: MutableLiveData<PackMainData> = MutableLiveData()


    fun getMultiplePackInfo(orderNo: String) {
        // 退款
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["orderNo"] = orderNo
            ApiClient.createApi<ShopNetWorkApi>()
                .getLogisticsByOrderNo(body.header(rKey), body.body(rKey))
                .onSuccess {
                    packMainDataLiveData.postValue(it)

                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }
}