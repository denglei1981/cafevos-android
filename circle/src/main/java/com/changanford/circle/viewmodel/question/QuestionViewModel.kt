package com.changanford.circle.viewmodel.question

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.QuestionInfoBean
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 * @Author : wenke
 * @Time : 2022/1/24 0024
 * @Description : QuestionViewModel
 */
class QuestionViewModel:BaseViewModel() {
    val questionInfoBean= MutableLiveData<QuestionInfoBean?>()
    /**
     * 我/TA的问答
     * [conQaUjId]被查看人的问答参与表id
     * */
    fun personalQA(conQaUjId:String){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["conQaUjId"]=conQaUjId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().personalQA(body.header(rKey), body.body(rKey)).onSuccess {
                questionInfoBean.postValue(it)
            }.onWithMsgFailure {
                it?.toast()
            }
        })
    }
}