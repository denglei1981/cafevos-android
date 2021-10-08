package com.changanford.shop.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.bean.GoodsTypesItemBean
import com.changanford.common.net.*
import com.changanford.common.repository.AdsRepository
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.base.BaseViewModel
import kotlinx.coroutines.launch

class ShopViewModel : BaseViewModel() {
    private var adsRepository: AdsRepository = AdsRepository(this)
    //广告
    var advertisingList: MutableLiveData<ArrayList<AdBean>> = adsRepository._ads
    //秒杀
    var KillListData =MutableLiveData<MutableList<GoodsItemBean>>()
    //分类
    var goodsClassificationData=MutableLiveData<MutableList<GoodsTypesItemBean>>()
    /**
     * 获取banner
    * */
    fun getBannerData(){
        adsRepository.getAds("商城广告位")
    }
    /**
     * 获取 商城首页
     * */
    fun getShopHomeData(){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.queryShopHomeData(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                KillListData.postValue(it)
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it?:"",MyApp.mContext)
            }
        }
    }
}