package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.PostsDetailBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap

class PostGraphicViewModel : BaseViewModel() {

    val postDetailsBean = MutableLiveData<PostsDetailBean>()

    fun getData(postsId: String) {
        launch (block ={
            val body = MyApp.mContext.createHashMap()
            body["postsId"] = postsId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getPostsDetail(body.header(rKey), body.body(rKey))
                .onSuccess {
                    postDetailsBean.value = it
                }
                .onFailure { }
        } )
    }

    fun getCommendList(bizId: String, page: Int) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["bizId"] = bizId
                it["type"] = "2"
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getCommentList(body.header(rKey), body.body(rKey))
                .onSuccess {

                }
                .onFailure { }
        })

    }
}