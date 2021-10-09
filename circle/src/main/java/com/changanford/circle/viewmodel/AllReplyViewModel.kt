package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.ChildCommentListBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.util.DeviceUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class AllReplyViewModel : BaseViewModel() {

    var commentBean = MutableLiveData<ChildCommentListBean>()
    val addCommendBean = MutableLiveData<CommonResponse<Any>>()
    val commentListBean = MutableLiveData<ArrayList<ChildCommentListBean>>()

    fun getListData(bizId: String, groupId: String, type: String, page: Int) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page.toString()
            body["pageSize"] = "20"
            body["queryParams"] = HashMap<String, Any>().also {
                it["bizId"] = bizId
                it["groupId"] = groupId
                it["type"] = type
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getChildCommentList(body.header(rKey), body.body(rKey)).also {
                    if (page == 1) {
                        if (it.data?.dataList?.size!! > 0) {
                            commentBean.value = it.data!!.dataList[0]
                            it.data!!.dataList.removeAt(0)
                            commentListBean.value = it.data!!.dataList
                        }
                    } else {
                        commentListBean.value = it.data!!.dataList
                    }
                }

        })
    }

    fun addPostsComment(
        bizId: String?,
        groupId: String?,
        pid: String?,
        content: String
    ) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["bizId"] = bizId ?: ""
            body["pid"] = pid ?: ""
            body["groupId"] = groupId ?: ""
            body["content"] = content
            body["phoneModel"] = DeviceUtils.getDeviceModel()

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .addPostsComment(body.header(rKey), body.body(rKey)).also {
                    addCommendBean.value = it
                }

        }, error = {
            it.message?.toast()
        })
    }
}