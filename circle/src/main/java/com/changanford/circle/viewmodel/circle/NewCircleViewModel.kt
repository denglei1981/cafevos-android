package com.changanford.circle.viewmodel.circle

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleMainBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.CirceHomeBean
import com.changanford.common.net.*
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 * @Author : wenke
 * @Time : 2022/1/5 0005
 * @Description : CircleViewModel
 */
class NewCircleViewModel:BaseViewModel() {
    val circleBean = MutableLiveData<CircleMainBean>()
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
                cirCleHomeData.postValue(it.data)
            }
        }, error = {
            it.message?.toast()
        })
    }

    fun communityIndex(lng: Double? = null, lat: Double? = null) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            lng?.let { body["lng"] = lng }
            lat?.let { body["lat"] = lat }

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .communityIndex(body.header(rKey), body.body(rKey)).onSuccess {
                    circleBean.value = it
                }

        }, error = {
            LiveDataBus.get().with(CircleLiveBusKey.REFRESH_CIRCLE_MAIN).postValue(false)
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