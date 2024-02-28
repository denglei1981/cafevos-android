package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.TagInfoBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.CommonResponse
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/10/11
 *Purpose
 */
class CreateCircleViewModel : BaseViewModel() {

    val upLoadBean = MutableLiveData<CommonResponse<Any>>()
    //创建圈子 -tagInfo
    val tagInfoData=MutableLiveData<TagInfoBean?>()
    /**
     * 创建圈子
     * [isAudit]是否审核
    * */
    fun createCircle(name: String,description: String, pic: String,tagIds:List<Int>,isAudit:Boolean,typeId:String?) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["description"] = description
            body["name"] = name
            body["pic"] = pic
            body["tagIds"] = tagIds
            body["needAudit"] = if(isAudit)"YES" else "NO"
            body["type"] = typeId?:""
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().createCircle(body.header(rKey), body.body(rKey))
                .also {
                    upLoadBean.value = it
                }
        }, error = {
            LiveDataBus.get().with(LiveDataBusKey.CREATE_CIRCLE_ERROR).postValue(it.message)
//            it.message.toString().toast()
        })
    }
    /**
     * 编辑圈子
    * */
    fun editCircle(circleId: String?,name: String,description: String, pic: String,tagIds:List<Int>,isAudit:Boolean,typeId:String?) {
        if(null==circleId)return
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["description"] = description
            body["name"] = name
            body["circleId"] = circleId
            body["pic"] = pic
            body["tagIds"] = tagIds
            body["needAudit"] = if(isAudit)"YES" else "NO"
            body["type"] = typeId?:""
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().editCircle(body.header(rKey), body.body(rKey))
                .also {
                    upLoadBean.value = it
                }
        }, error = {
            LiveDataBus.get().with(LiveDataBusKey.CREATE_CIRCLE_ERROR).postValue(it.message)
//            it.message.toString().toast()
        })
    }
    /**
     * 获取标签信息
     * */
    fun getTagInfo(){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().circleCreateInfo(body.header(rKey), body.body(rKey)).also {
                it.data?.apply {tagInfoData.postValue(it.data) }
            }
        }, error = {
            it.message?.toast()
        })
    }
}