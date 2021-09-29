package com.changanford.shop.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.bean.GoodsTypesBean
import com.changanford.common.net.*
import com.changanford.common.repository.AdsRepository
import kotlinx.coroutines.launch

class ShopViewModel : ViewModel() {
    private val body = HashMap<String, Any>()
    private var adsRepository: AdsRepository = AdsRepository(this)
    //广告
    var advertisingList: MutableLiveData<ArrayList<AdBean>> = adsRepository._ads
    //秒杀
    var KillListData =MutableLiveData<MutableList<GoodsItemBean>>()
    //分类
    var goodsClassificationData=MutableLiveData<MutableList<GoodsTypesBean>>()
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
                apiService.queryGoodsKillData(body.header(randomKey), body.body(randomKey))
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
                apiService.queryGoodsClassification(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                goodsClassificationData.postValue(it)
            }
        }
    }
}