package com.changanford.shop.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.bean.GoodsTypesItemBean
import com.changanford.common.net.*
import com.changanford.common.repository.AdsRepository
import com.changanford.common.utilext.createHashMap
import com.changanford.shop.base.BaseViewModel
import com.tencent.mm.opensdk.utils.Log
import kotlinx.coroutines.launch

class ShopViewModel : BaseViewModel() {
    private val body = MyApp.mContext.createHashMap()
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
     * 获取 商城首页秒杀列表
     * */
    fun getShopHomeKillData(){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.queryGoodsKillData(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                KillListData.postValue(it)
            }
        }
    }
    /**
     * 一级分类列表
     * */
    fun getGoodsTypeList(){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.queryGoodsClassification(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                Log.e("okhttp","onSuccess>>>>:$it")
                goodsClassificationData.postValue(it?.dataList)
            }.onFailure {
                Log.e("okhttp","onFailure>>>>:$it")
            }
        }
    }
}