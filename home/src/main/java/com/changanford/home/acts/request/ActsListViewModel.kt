package com.changanford.home.acts.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.toastShow
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.ListMainBean
import com.changanford.home.data.ActBean
import com.changanford.home.data.EnumBean

/**
 *   活动查询列表。
 * */
class ActsListViewModel : BaseViewModel() {
    val actsLiveData = MutableLiveData<UpdateUiState<ListMainBean<ActBean>>>() //


    var zonghescreens = MutableLiveData<List<EnumBean>>() //综合排序等
    var screenstype = MutableLiveData<List<EnumBean>>()  //进行中等
    var guanfang = MutableLiveData<List<EnumBean>>()  //官方
    var xianshang = MutableLiveData<List<EnumBean>>()  //线上线下

    // 发布方， 排序， 线上线下，活动状态
    var shaixuanList =
        arrayListOf("OrderTypeEnum", "ActivityTimeStatus", "OfficialEnum", "WonderfulTypeEnum")

    fun getActList(
        page: Boolean,
        count: Int,
        pageSize: Int
    ) {
        launch(false, {
            var body = HashMap<String, Any>()
            body["page"] = page
            body["pageNo"] = count
            body["pageSize"] = pageSize
//            var hashMap = HashMap<String, Any>()
//            if (choosezonghe.code != "") {
//                hashMap["orderType"] = choosezonghe.code
//            }
//            if (chooseact.code != "-1") {
//                hashMap["activityTimeStatus"] = chooseact.code
//            }
//            if (chooseguanfang.code is Number) {
//                hashMap["official"] = (chooseguanfang.code as Number).toInt()
//            }
//            if (choosexianshang.code is Number) {
//                hashMap["wonderfulType"] = (choosexianshang.code as Number).toInt()
//                if ((choosexianshang.code as Number).toInt() == 1) {
//                    if (choosecityx.regionName != "") {
//                        hashMap["cityId"] = choosecityx.regionId
//                        hashMap["cityName"] = choosecityx.regionName
//                    }
//                }
//            }
//            body["queryParams"] = hashMap
            var rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getHighlights(body.header(rkey), body.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<ListMainBean<ActBean>>(it, true, "")
                    actsLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<ListMainBean<ActBean>>(false, it)
                    actsLiveData.postValue(updateUiState)
                }.onFailure {
                    val updateUiState = UpdateUiState<ListMainBean<ActBean>>(false, "错了。。")
                    actsLiveData.postValue(updateUiState)
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
                            screenstype.value = it
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
}





