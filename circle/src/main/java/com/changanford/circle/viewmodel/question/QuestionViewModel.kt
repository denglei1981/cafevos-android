package com.changanford.circle.viewmodel.question

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.NewCircleDataBean
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 * @Author : wenke
 * @Time : 2022/1/24 0024
 * @Description : QuestionViewModel
 */
class QuestionViewModel:BaseViewModel() {
    //圈子列表
    val circleListData= MutableLiveData<NewCircleDataBean?>()
    /**
     * 我/TA的问答
     * */
    fun personalQA(){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().circleHome(body.header(rKey), body.body(rKey)).onSuccess {
            }.onWithMsgFailure {
                it?.toast()
            }
        })
    }
}