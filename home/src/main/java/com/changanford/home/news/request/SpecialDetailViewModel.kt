package com.changanford.home.news.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.SpecialCarListBean
import com.changanford.common.net.*
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.news.data.SpecialDetailData
import com.xiaomi.push.it

/**
 *  专题详情
 * */
class SpecialDetailViewModel : BaseViewModel() {
    val specialDetailLiveData = MutableLiveData<UpdateUiState<SpecialDetailData>>() // 专题详情
    val carListBean = MutableLiveData<ArrayList<SpecialCarListBean>>()

    /**
     *  专题列表顶部
     * */
    fun getSpecialDetail(specialTopicId: String) {

        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["specialTopicId"] = specialTopicId
//            requestBody["specialTopicId"] = specialTopicId
//            requestBody["pageNo"] = page
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getSpecialTopicList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    if (it?.isBuyCarGuide == 1) {
                        getCarModelList()
                    }
                    val updateUiState = UpdateUiState<SpecialDetailData>(it, true, "")
                    specialDetailLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<SpecialDetailData>(false, "")
                    specialDetailLiveData.postValue(updateUiState)
                }
        })
    }

    fun getSpecialCarDetail(specialTopicId: String, carModelId: Int) {

        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["specialTopicId"] = specialTopicId
//            requestBody["pageNo"] = page
//            requestBody["pageSize"] = 20
            if (carModelId > 0) {
                requestBody["carModelId"] = carModelId
            }
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getSpecialTopicList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<SpecialDetailData>(it, true, "")
                    specialDetailLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<SpecialDetailData>(false, "")
                    specialDetailLiveData.postValue(updateUiState)
                }
        })
    }

    private fun getCarModelList() {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["type"] = "1"
            val rkey = getRandomKey()
            ApiClient.createApi<NetWorkApi>()
                .getCarModelList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    carListBean.value = it
                }.onWithMsgFailure {

                }
        })
    }

}