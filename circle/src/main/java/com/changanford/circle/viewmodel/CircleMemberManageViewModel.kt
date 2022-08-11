package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap

/**
 *Author lcw
 *Time on 2021/10/14
 *Purpose
 */
class CircleMemberManageViewModel : BaseViewModel() {

    val personalBean = MutableLiveData<ArrayList<CircleMemberBean>>()
    var total = MutableLiveData<Int>()
    var setStarsRoleBean = MutableLiveData<CommonResponse<Any>>()
    var deletePersonalBean = MutableLiveData<CommonResponse<Any>>()

    fun getData(circleId: String, page: Int) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, String>().also {
                it["circleId"] = circleId
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getCircleUsers(body.header(rKey), body.body(rKey))
                .onSuccess {
                    val list: ArrayList<CircleMemberBean> = ArrayList()
                    if (it?.total != 0) {
                        total.value = it?.total?.minus(1)
                        it?.dataList?.let { it1 -> list.addAll(it1) }
                        it?.dataList?.forEachIndexed { _, circleMemberBean ->
                            if (circleMemberBean.starOrderNumStr == "圈主") {
                                list.remove(circleMemberBean)
                            }
                        }
                    }
                    personalBean.value = list
                }
                .onFailure {
                    val list: ArrayList<CircleMemberBean> = ArrayList()
                    personalBean.value = list
                }
        })
    }

    /**
     * 圈子 ：设置管理员
     */
    fun setStarsRole(
        circleId: String,
        circleStarRoleId: String,
        userIds: Array<String?>,
    ) {
        launch(block = {
            val body = HashMap<String, Any>()
            body["circleId"] = circleId
            body["circleStarRoleId"] = circleStarRoleId
            body["userIds"] = userIds
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().setStarsRole(body.header(rKey), body.body(rKey))
                .also {
                    setStarsRoleBean.value = it
                }
        })

    }

    /**
     * 圈子 ：删除成员
     */
    fun deleteCircleUsers(
        circleId: String,
        userIds: Array<String?>
    ) {
        launch(block = {
            val body = HashMap<String, Any>()
            body["circleId"] = circleId
            body["userIds"] = userIds
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().deleteCircleUsers(body.header(rKey), body.body(rKey))
                .also {
                    deletePersonalBean.value = it
                }
        })

    }
}