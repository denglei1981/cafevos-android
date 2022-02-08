package com.changanford.circle.ui.ask.request

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.ChildCommentListBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.CancelReasonBeanItem
import com.changanford.common.bean.QuestionData
import com.changanford.common.bean.STSBean
import com.changanford.common.net.*
import com.changanford.common.util.DeviceUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class QuestionViewModel : BaseViewModel() {

    var  questTypeList: MutableLiveData<ArrayList<QuestionData>> = MutableLiveData()

    var  fordRewardList: MutableLiveData<ArrayList<QuestionData>> = MutableLiveData()

    val stsBean = MutableLiveData<STSBean>()

    var createQuestionLiveData= MutableLiveData<String>()
    fun getQuestionType() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["dictType"] = "qa_question_type"
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getQuestionType(body.header(rKey), body.body(rKey)).also {
                    questTypeList.postValue(it.data)
                }
        })
    }

    fun getFordReward(){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["dictType"] = "qa_fb_reward"
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getQuestionType(body.header(rKey), body.body(rKey)).also {
                    fordRewardList.postValue(it.data)
                }
        })
    }
    fun getOSS(){
        launch(block =  {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<NetWorkApi>().getOSS(body.header(rKey),body.body(rKey))
                .onSuccess {
                    stsBean.value= it
                }
                .onFailure {

                }
        })
    }

    fun createQuestion(params: HashMap<String,Any>){
        launch (block = {
            val body = params

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().createQuestion(body.header(rKey),body.body(rKey))
                .onSuccess {
                    createQuestionLiveData.value = "upsuccess"
                }
                .onWithMsgFailure {
                    it?.toast()
                    createQuestionLiveData.value="error"
                }
        })
    }


    fun getInitQuestion(){
        launch (block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getInitQuestion(body.header(rKey),body.body(rKey))
                .onSuccess {
                }
                .onWithMsgFailure {
                    it?.toast()

                }
        })
    }








}