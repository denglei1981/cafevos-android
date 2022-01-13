package com.changanford.circle.viewmodel.circle

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.CirCleHotList
import com.changanford.common.bean.CirceHomeBean
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.bean.NewCircleDataBean
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 * @Author : wenke
 * @Time : 2022/1/5 0005
 * @Description : CircleViewModel
 */
class NewCircleViewModel:BaseViewModel() {
    val cirCleHomeData=MutableLiveData<CirceHomeBean>()
    //猜你喜欢
    val youLikeData=MutableLiveData<MutableList<NewCircleBean>?>()
    //热门榜单分类
    val hotTypesData=MutableLiveData<MutableList<CirCleHotList>?>()
    //圈子列表
    val circleListData=MutableLiveData<NewCircleDataBean?>()
    /**
     * 圈子首页
    * */
    fun getCircleHomeData(){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().circleHome(body.header(rKey), body.body(rKey)).onSuccess {
                it?.apply {cirCleHomeData.postValue(this)  }
            }.onWithMsgFailure { it?.toast() }
        })
    }
    /**
     * 获取猜你喜欢的数据
    * */
    fun getYouLikeData(){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().youLike(body.header(rKey), body.body(rKey)).onSuccess {
                 it?.apply { youLikeData.postValue(this.dataList) }
                }.onWithMsgFailure { it?.toast() }
        })
    }
    /**
     * 获取热门榜单分类
     * */
    fun getHotTypes(){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().circleHotTypes(body.header(rKey), body.body(rKey)).onSuccess {
                it?.apply { hotTypesData.postValue(this) }
            }.onWithMsgFailure { it?.toast()  }
        })
    }
    /**
     * 获取热门榜单列表
     * */
    fun getHotList(topId:Int,pageNo:Int){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"]=pageNo
            body["queryParams"]=HashMap<String,Any>().also {
                it["topId"]=topId
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().circleHotList(body.header(rKey), body.body(rKey)).onSuccess {
                it?.apply { circleListData.postValue(this) }
            }.onWithMsgFailure {
                it?.toast()
            }
        })
    }
}