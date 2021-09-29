package com.changanford.shop.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.changanford.common.bean.AdBean
import com.changanford.common.repository.AdsRepository

class ShopViewModel : ViewModel() {
   private var adsRepository: AdsRepository = AdsRepository(this)
    var advertisingList: MutableLiveData<ArrayList<AdBean>> = adsRepository._ads
    /**
     * 获取banner
    * */
    fun getBannerData(){
        adsRepository.getAds("商城广告位")
    }
}