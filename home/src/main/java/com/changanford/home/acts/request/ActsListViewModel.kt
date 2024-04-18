package com.changanford.home.acts.request

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.ActBean
import com.changanford.common.bean.AdBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.util.SafeMutableLiveData
import com.changanford.common.utilext.toastShow
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.ListMainBean
import com.changanford.home.data.EnumBean

/**
 *   活动查询列表。
 * */
class ActsListViewModel : BaseViewModel() {
    val actsLiveData = SafeMutableLiveData<UpdateUiState<ListMainBean<ActBean>>>() //
    var zonghescreens = SafeMutableLiveData<List<EnumBean>>() //综合排序等
    var screenstype = SafeMutableLiveData<MutableList<EnumBean>>()  //进行中等
    var guanfang = SafeMutableLiveData<List<EnumBean>>()  //官方
    var xianshang = SafeMutableLiveData<List<EnumBean>>()  //线上线下

    // 发布方， 排序， 线上线下，活动状态
    var shaixuanList =
        arrayListOf("OrderTypeEnum", "ActivityTimeStatus", "OfficialEnum", "WonderfulTypeEnum")

    var pageNo = 1
    fun getActList(
        isLoadMore: Boolean = false,
        cityId: String = "",// 城市id
        cityName: String = "",// 城市名称
        wonderfulType: Int = -1,// 线上，线下， 问卷调查。0-线上，1-线下，2-问卷,4可用
        orderType: String = "",//排序 综合排序  COMPREHENSIVE HOT,New
        official: Int = -1,//0-官方，1-非官方,2-经销商,可用
        activityTimeStatus: String = "" // 过期，还是进行中。ON_GOING CLOSED
    ) {
        launch(false, {
            var body = HashMap<String, Any>()
            body["page"] = true
            if(isLoadMore){
                pageNo+=1
            }else{
                pageNo=1
            }
            body["pageNo"] = pageNo
            body["pageSize"] = PageConstant.DEFAULT_PAGE_SIZE_THIRTY
            var hashMap = HashMap<String, Any>()
            if (!TextUtils.isEmpty(orderType)) {
                hashMap["orderType"] = orderType
            }
            if (!TextUtils.isEmpty(activityTimeStatus)) {
                hashMap["activityTimeStatus"] = activityTimeStatus
            }
            if (official >= 0) {
                hashMap["official"] = official
            }
            if (wonderfulType >= 0) {
                hashMap["wonderfulType"] = wonderfulType
                if (wonderfulType == 1) {
                    if (!TextUtils.isEmpty(cityId) || !TextUtils.isEmpty(cityName)) {
                        hashMap["cityId"] = cityId
                        hashMap["cityName"] = cityName
                    }
                }
            }
            if (hashMap.size > 0) {
                body["queryParams"] = hashMap
            }
            var rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getHighlightsV2(body.header(rkey), body.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<ListMainBean<ActBean>>(it, true, isLoadMore,"")
                    actsLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<ListMainBean<ActBean>>(false, it)
                    actsLiveData.postValue(updateUiState)
                }.onFailure {
//                    val updateUiState = UpdateUiState<ListMainBean<ActBean>>(false, "")
//                    actsLiveData.postValue(updateUiState)
                }
        })
    }

    /**
     * 点击活动统计
     */
    fun AddACTbrid(wonderfulId: Int) {
        launch(false, {
            var body = HashMap<String, Any>()
            body["wonderfulId"] = wonderfulId
            var rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .addactbrid(body.header(rkey), body.body(rkey))
                .onSuccess {
                }.onWithMsgFailure {
                }.onFailure {
                }

        })
    }


    /**
     *  查询活动枚举。
     * */
    fun getEnum(className: String) {
        launch(false, {
            var body = HashMap<String, Any>()
            body["className"] = className
            var rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getEnum(body.header(rkey), body.body(rkey))
                .onSuccess {
                    when (className) {
                        shaixuanList[0] -> {
                            zonghescreens.value = it
                        }
                        shaixuanList[1] -> {
                            screenstype.value = it as? MutableList<EnumBean>
                            screenstype.value?.add(0,EnumBean("", "全部活动"))
                        }
                        shaixuanList[2] -> {
                            guanfang.value = it
                        }
                        shaixuanList[3] -> {
                            xianshang.value = it
                        }
                    }

                }.onWithMsgFailure {
                    toastShow(it!!)
                }.onFailure {

                }

        })
    }

    val bannerLiveData = MutableLiveData<UpdateUiState<List<AdBean>>>() //
    fun getBanner() {
        launch(false, {
            val body = HashMap<String, Any>()
            val rkey = getRandomKey()
            body["posCode"] = "activiity_list_topad"
            ApiClient.createApi<HomeNetWork>()
                .adsLists(body.header(rkey), body.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<List<AdBean>>(it, true, "")
                    bannerLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    if (it != null) {
                        toastShow(it)
                    }
                }.onFailure {
//                    val updateUiState = UpdateUiState<String>(false, it)
//                    bannerLiveData.postValue(updateUiState)
                }


        })


    }

}





