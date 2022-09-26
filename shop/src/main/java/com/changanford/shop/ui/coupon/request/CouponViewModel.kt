package com.changanford.shop.ui.coupon.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.CouponMiddleData
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.bean.ListMainBean
import com.changanford.common.net.*
import com.changanford.common.net.response.UpdateUiState
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.shop.api.ShopNetWorkApi
import com.changanford.shop.bean.CouponData
import kotlinx.coroutines.launch


class CouponViewModel : BaseViewModel() {

    var couponListLiveData: MutableLiveData<UpdateUiState<ListMainBean<CouponsItemBean>>> =
        MutableLiveData()
    var couponOverListLiveData: MutableLiveData<UpdateUiState<ListMainBean<CouponsItemBean>>> =
        MutableLiveData()
    var couponInvalidListLiveData: MutableLiveData<UpdateUiState<ListMainBean<CouponsItemBean>>> =
        MutableLiveData()
    var page = 1
    fun getCouponList(isLoadMore: Boolean, type: Int) {

        launch(block = {
            val body = MyApp.mContext.createHashMap()
            if (isLoadMore) {
                page += 1
            } else {
                page = 1
            }
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["states"] = type
            }
            val rKey = getRandomKey()
            ApiClient.createApi<ShopNetWorkApi>().getCouponList(body.header(rKey), body.body(rKey))
                .onSuccess {
                    val updateUiState =
                        UpdateUiState<ListMainBean<CouponsItemBean>>(it, true, isLoadMore, "")
                    when (type) {
                        1 -> {
                            couponListLiveData.postValue(updateUiState)
                        }
                        2 -> {
                            couponOverListLiveData.postValue(updateUiState)
                        }
                        3 -> {
                            couponInvalidListLiveData.postValue(updateUiState)
                        }

                    }

                }
                .onWithMsgFailure {
                    val updateUiState =
                        UpdateUiState<ListMainBean<CouponsItemBean>>(false, it, isLoadMore)
                    when (type) {
                        1 -> {
                            couponListLiveData.postValue(updateUiState)
                        }
                        2 -> {
                            couponOverListLiveData.postValue(updateUiState)
                        }
                        3 -> {
                            couponInvalidListLiveData.postValue(updateUiState)
                        }

                    }
                    it?.toast()
                }
        })
    }

    fun getCouponMiddlePageData(result: (CouponMiddleData) -> Unit) {
        viewModelScope.launch {
            fetchRequest {
                val body = HashMap<String, Any>()
                val rkey = getRandomKey()
                body["posCodes"] =
                    "my_info_coupon_carousel,my_info_coupon_fuyu,my_info_coupon_other"
                apiService
                    .getMoreBanner(body.header(rkey), body.body(rkey))
                    .onSuccess {
                        it?.run { result(it) }
                    }.onWithMsgFailure {
                        it?.toast()
                    }.onFailure {
//                    val updateUiState = UpdateUiState<String>(false, it)
//                    bannerLiveData.postValue(updateUiState)
                    }
            }
        }
    }
}