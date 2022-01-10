package com.changanford.circle.viewmodel.circle

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleMainBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.CirceHomeBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 * @Author : wenke
 * @Time : 2022/1/5 0005
 * @Description : CircleViewModel
 */
class NewCircleViewModel:BaseViewModel() {
    //猜你喜欢
    val youLikeData=MutableLiveData<CircleMainBean>()
    val cirCleHomeData=MutableLiveData<CirceHomeBean>()
    /**
     * 圈子首页
    * */
    fun getCircleHomeData(){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().circleHome(body.header(rKey), body.body(rKey)).also {
                it.data?.apply {cirCleHomeData.postValue(this)  }
            }
        }, error = {
            it.message?.toast()
        })
    }
    /**
     * 获取猜你喜欢的数据
    * */
    fun getYouLikeData(){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().youLike(body.header(rKey), body.body(rKey)).also {
//                youLikeData.postValue()
                }
        }, error = {
            it.message?.toast()
        })
    }
}