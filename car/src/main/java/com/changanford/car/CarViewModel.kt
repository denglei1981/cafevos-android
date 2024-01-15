package com.changanford.car

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.*
import com.changanford.common.net.*
import com.changanford.common.net.response.UpdateUiState
import com.changanford.common.repository.AdsRepository
import com.changanford.common.util.MConstant.userId
import com.changanford.common.util.paging.DataRepository
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.xiaomi.push.it
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CarViewModel : BaseViewModel() {
    var adsRepository: AdsRepository = AdsRepository(this)
    var adsBottomRepository = AdsRepository(this)
    var _ads: MutableLiveData<ArrayList<AdBean>> = MutableLiveData<ArrayList<AdBean>>()
    var bottomAds: MutableLiveData<ArrayList<AdBean>> = MutableLiveData<ArrayList<AdBean>>()
    var _middleInfo: MutableLiveData<MiddlePageBean> = MutableLiveData<MiddlePageBean>()

    //首页顶部banenr
    val topBannerBean = MutableLiveData<MutableList<NewCarBannerBean>?>()

    //爱车首页
    val carInfoBean = MutableLiveData<MutableList<NewCarInfoBean>?>()

    //更多车型
    val carMoreInfoBean = MutableLiveData<CarMoreInfoBean?>()

    //认证信息
    val carAuthBean = MutableLiveData<CarAuthBean?>()

    //经销商信息
    val dealersBean = MutableLiveData<NewCarInfoBean?>()

    //提车日记
    val carHistoryBean = MutableLiveData<PostBean>()

    //购车引导
    val buyCarTipsBean = MutableLiveData<SpecialDetailData?>()

    init {
        _ads = adsRepository._ads
        bottomAds = adsBottomRepository._ads
    }

    fun getTopAds() {
        adsRepository.getAds("uni_topbanner")
    }

    fun getBottomAds(){
        adsBottomRepository.getAds("fordpai_buycar_center_ads")
    }

    fun getMyCar() {
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val rkey = getRandomKey()
                apiService.getMiddlePageInfo(hashMap.header(rkey), hashMap.body(rkey))
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
    fun getTopBanner() {
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val rkey = getRandomKey()
                apiService.getCarTopBanner(hashMap.header(rkey), hashMap.body(rkey))
            }.onSuccess {
                topBannerBean.postValue(it)
            }.onWithMsgFailure {
                topBannerBean.postValue(null)
                it?.toast()
            }
        }
    }

    /**
     * 获取爱车首页
     * */
    fun getMyCarModelList() {
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val randomKey = getRandomKey()
                apiService.getMyCarModelList(hashMap.header(randomKey), hashMap.body(randomKey))
            }.onSuccess {
                carInfoBean.postValue(it)
            }.onWithMsgFailure {
                it?.toast()
                carInfoBean.postValue(null)
            }
        }
    }

    fun getCarHistory(carModelIds: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = 1
            body["pageSize"] = 5
            body["queryParams"] = HashMap<String, Any>().also {
                it["topicId"] = "0"
                it["carModelIds"] = carModelIds
            }
            val rKey = getRandomKey()
            ApiClient.createApi<NetWorkApi>().getPosts(body.header(rKey), body.body(rKey))
                .onSuccess {
                    carHistoryBean.value = it
                }
        })
    }

    fun getBuyCarTips(carModelId: String) {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["specialTopicId"] = "0"
            requestBody["carModelId"] = carModelId
            val rkey = getRandomKey()
            ApiClient.createApi<NetWorkApi>()
                .getSpecialTopicList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    buyCarTipsBean.value = it
                }
        })
    }

    /**
     * 开启赏车之旅-推荐车型
     * */
    fun getMoreCar() {
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val randomKey = getRandomKey()
                apiService.getMoreCareInfo(hashMap.header(randomKey), hashMap.body(randomKey))
            }.onSuccess {
                carMoreInfoBean.postValue(it)
            }.onWithMsgFailure {
                carMoreInfoBean.postValue(null)
                it?.toast()
            }
        }
    }

    /**
     * 认证信息
     * */
    fun getAuthCarInfo() {
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val randomKey = getRandomKey()
                apiService.queryAuthCarList(hashMap.header(randomKey), hashMap.body(randomKey))
            }.onSuccess {
                carAuthBean.postValue(it)
            }.onWithMsgFailure {
                carAuthBean.postValue(null)
                it?.toast()
            }
        }
    }

    /**
     * 获取最近的一家经销商
     * [lngX]经度
     * [latY]纬度
     * [carModelCode]车型编码
     * */
    fun getRecentlyDealers(
        lngX: Double? = null,
        latY: Double? = null,
        carModelId: String,
        carModelCode: String? = null
    ) {
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                if (lngX != null && latY != null) {
                    hashMap["lngX"] = lngX
                    hashMap["latY"] = latY
                } else hashMap["tryIp"] = true
                hashMap["carModelId"] = carModelId
                if (null != carModelCode) hashMap["carModelCode"] = carModelCode
                val randomKey = getRandomKey()
                apiService.getRecentlyDealers(hashMap.header(randomKey), hashMap.body(randomKey))
            }.onSuccess {
                dealersBean.postValue(it)
            }.onWithMsgFailure {
                dealersBean.postValue(null)
                it?.toast()
            }
        }
    }


    /**
     * 爱车活动
     */
    //推荐爱车活动列表
    fun getLoveCarRecommendList(result: (ArrayList<ActivityListBean>) -> Unit) {
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val randomKey = getRandomKey()
                apiService.loveCarRecommendList(hashMap.header(randomKey), hashMap.body(randomKey))
            }.onSuccess {
                it?.let {
                    result(it)
                }
            }
        }
    }

    //爱车活动列表
    fun getLoveCarActivityList(result: (ArrayList<LoveCarActivityListBean>) -> Unit) {
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val randomKey = getRandomKey()
                apiService.loveCarActivityList(hashMap.header(randomKey), hashMap.body(randomKey))
            }.onSuccess {
                it?.let {
                    result(it)
                }
            }
        }
    }

    /**
     * 爱车活动顶部banner
     */
    fun getLoveCarConfig(result: (List<String>) -> Unit) {
        viewModelScope.launch {
            fetchRequest {
                var body = java.util.HashMap<String, Any>()
                body["configKey"] = "love_car_adsense"
                body["obj"] = true
                var rkey = getRandomKey()
                apiService.getLoveCarConfig(body.header(rkey), body.body(rkey))
            }.onSuccess {
                it?.let {
                    var pics = (it.get("imgList") as String).split(",")
                    result(pics)
                }
            }
        }
    }
}