package com.changanford.circle.viewmodel

import com.changanford.circle.api.CircleNetWork
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2022/8/18
 *Purpose
 */
class CreateCircleTopicViewModel : BaseViewModel() {

    fun initiateTopic(
        circleId: String,
        name: String,
        description: String,
        pic: String,
        block: () -> Unit
    ) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["circleId"] = circleId
            body["description"] = description
            body["name"] = name
            body["pic"] = pic

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .initiateTopic(body.header(rKey), body.body(rKey))
                .also {
                    it.msg.toast()
                    if (it.code == 0) {
                        block.invoke()
                    }
                }
        })
    }

    fun updateTopic(
        topicId: String,
        name: String,
        description: String,
        pic: String,
        block: () -> Unit
    ) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["topicId"] = topicId
            body["description"] = description
            body["name"] = name
            body["pic"] = pic

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .updateTopic(body.header(rKey), body.body(rKey))
                .also {
                    it.msg.toast()
                    if (it.code == 0) {
                        block.invoke()
                    }
                }
        })
    }

}