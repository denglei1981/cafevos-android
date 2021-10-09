package com.changanford.car

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.MiddlePageBean
import com.changanford.common.bean.RecommendData
import com.changanford.common.net.*
import com.changanford.common.repository.AdsRepository
import com.changanford.common.util.paging.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CarViewModel : ViewModel() {
    var adsRepository: AdsRepository = AdsRepository(this)
    var _ads: MutableLiveData<ArrayList<AdBean>> = MutableLiveData<ArrayList<AdBean>>()
    var _middleInfo: MutableLiveData<MiddlePageBean> = MutableLiveData<MiddlePageBean>()

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
                var map = HashMap<String, Any>()
                map["pageNo"] = pageNum
                map["pageSize"] = pageSize
                val hashMap = HashMap<String, Any>()
                hashMap["pageNo"] = pageNum.toString()
                hashMap["pageSize"] = pageSize.toString()
                hashMap["queryParams"] = HashMap<String, Any>().also {
                    it["isHotSelected"] = "1"
                }
                var rkey = getRandomKey()
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
                var rkey = getRandomKey()
                apiService.getMiddlePageInfo(hashMap.header(rkey),hashMap.body(rkey))
            }.onSuccess {
                _middleInfo.postValue(it)
            }.onFailure {
                _middleInfo.postValue(null)
            }
        }
    }
}