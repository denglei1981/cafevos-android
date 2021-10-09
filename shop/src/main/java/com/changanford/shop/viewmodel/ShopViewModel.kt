package com.changanford.shop.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.bean.GoodsTypesItemBean
import com.changanford.common.repository.AdsRepository
import com.changanford.shop.base.BaseViewModel

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

}