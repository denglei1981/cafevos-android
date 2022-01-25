package com.changanford.car

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.changanford.common.bean.*
import com.changanford.common.net.*
import com.changanford.common.repository.AdsRepository
import com.changanford.common.util.paging.DataRepository
import com.changanford.common.utilext.toast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CarViewModel : ViewModel() {
    var adsRepository: AdsRepository = AdsRepository(this)
    var _ads: MutableLiveData<ArrayList<AdBean>> = MutableLiveData<ArrayList<AdBean>>()
    var _middleInfo: MutableLiveData<MiddlePageBean> = MutableLiveData<MiddlePageBean>()
    //首页顶部banenr
    val topBannerBean= MutableLiveData<MutableList<NewCarBannerBean>?>()
    //爱车首页
    val carInfoBean=MutableLiveData<MutableList<NewCarInfoBean>?>()
    //更多车型
    val carMoreInfoBean=MutableLiveData<CarMoreInfoBean?>()
    init {
        _ads = adsRepository._ads
    }

    fun getTopAds() {
        adsRepository.getAds("uni_topbanner")
    }

    /**
     * 使用paging获取接口数据，默认每页10条
     */
    fun getRecommendList(): Flow<PagingData<RecommendData>> {
        return DataRepository.getDataInfo(query = { pageNum, pageSize ->
            fetchRequest(pageNum == 1) {
                val map = HashMap<String, Any>()
                map["pageNo"] = pageNum
                map["pageSize"] = pageSize
                val hashMap = HashMap<String, Any>()
                hashMap["pageNo"] = pageNum.toString()
                hashMap["pageSize"] = pageSize.toString()
                hashMap["queryParams"] = HashMap<String, Any>().also {
                    it["isHotSelected"] = "1"
                }
                val rkey = getRandomKey()
                apiService.getRecommendList(map.header(rkey), map.body(rkey))
            }
        }, list = {
            it?.dataList
        }).cachedIn(viewModelScope)
    }

    fun getMyCar(){
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val rkey = getRandomKey()
                apiService.getMiddlePageInfo(hashMap.header(rkey),hashMap.body(rkey))
            }.onSuccess {
                _middleInfo.postValue(it)
            }.onFailure {
                _middleInfo.postValue(null)
            }
        }
    }
    fun queryAuthCarAndIncallList(result: (CommonResponse<CarAuthBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                val body = HashMap<String, Any>()
                val rkey = getRandomKey()
                apiService.queryAuthCarList(body.header(rkey), body.body(rkey))
            })
        }
    }
    /**
     * 获取爱车首页顶部banner
    * */
    fun getTopBanner(){
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val rkey = getRandomKey()
                apiService.getCarTopBanner(hashMap.header(rkey),hashMap.body(rkey))
            }.onSuccess {
                topBannerBean.postValue(it)
            }.onWithMsgFailure {
                it?.toast()
            }
        }
    }
    /**
     * 获取爱车首页
     * */
    fun getMyCarModelList(carModelCode:String?=null){
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
//                hashMap["carModelCode"]=carModelCode
                val randomKey = getRandomKey()
                apiService.getMyCarModelList(hashMap.header(randomKey),hashMap.body(randomKey))
            }.onSuccess {
                carInfoBean.postValue(it)
            }.onWithMsgFailure {
                it?.toast()
                carInfoBean.postValue(null)
            }
        }
    }
    /**
     * 开启赏车之旅
    * */
    fun getMoreCar(){
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val randomKey = getRandomKey()
                apiService.getMoreCareInfo(hashMap.header(randomKey),hashMap.body(randomKey))
            }.onSuccess {
                carMoreInfoBean.postValue(it)
            }.onFailure {
                carMoreInfoBean.postValue(null)
            }
        }
    }
}