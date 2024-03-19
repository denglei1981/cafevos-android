package com.changanford.circle.ui.ask.request

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.AskListMainData
import com.changanford.circle.bean.HomeDataListBean
import com.changanford.circle.bean.MechanicData
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.QuestionInfoBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.net.response.UpdateUiState
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class AskRecommendViewModel : BaseViewModel() {


    var mechanicLiveData: MutableLiveData<MechanicData> = MutableLiveData()


    var questionListLiveData: MutableLiveData<UpdateUiState<HomeDataListBean<AskListMainData>>> =
        MutableLiveData()


//    var  errorLiveData: MutableLiveData<String> = MutableLiveData()


    fun getInitQuestion() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getInitQuestion(body.header(rKey), body.body(rKey))
                .onSuccess {
                    mechanicLiveData.postValue(it)
                }
                .onWithMsgFailure {
                    it?.toast()

                }
        })
    }


    var page: Int = 1
    fun getQuestionList(isLoadMore: Boolean, questionTypes: MutableList<String>) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            if (isLoadMore) {
                page += 1
            } else {
                page = 1
            }
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                if (questionTypes.size > 0) {
                    it["questionTypes"] = questionTypes
                }
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getRecommendQuestionList(body.header(rKey), body.body(rKey))
                .onSuccess {

                    val updateUiState =
                        UpdateUiState<HomeDataListBean<AskListMainData>>(it, true, isLoadMore, "")
                    questionListLiveData.postValue(updateUiState)
                }
                .onWithMsgFailure {
                    val updateUiState =
                        UpdateUiState<HomeDataListBean<AskListMainData>>(false, it, isLoadMore)
                    questionListLiveData.postValue(updateUiState)
                    it?.toast()

                }
        })
    }

    var questionListBean: MutableLiveData<QuestionInfoBean?> = MutableLiveData()
    fun questionOfPersonal(conQaUjId: String) {
        launch(block = {
            body.clear()
            body["pageNo"] = 1
            body["pageSize"] = 5
            body["queryParams"] = HashMap<String, Any>().also {
                it["conQaUjId"] = conQaUjId
                it["personalPageType"] = "QUESTION"
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .questionOfPersonal(body.header(rKey), body.body(rKey)).onSuccess {
                questionListBean.postValue(it)
            }.onWithMsgFailure {
                it?.toast()
            }
        })
    }

}