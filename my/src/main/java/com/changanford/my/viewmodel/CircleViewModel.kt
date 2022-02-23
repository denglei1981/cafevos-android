package com.changanford.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.*
import com.changanford.common.net.*
import kotlinx.coroutines.launch

/**
 *  文件名：CircleViewModel
 *  创建者: zcy
 *  创建日期：2021/9/26 19:51
 *  描述: TODO
 *  修改描述：TODO
 */
class CircleViewModel : ViewModel() {
    /**
     * 我管理的圈子
     */
    var mMangerCircle: MutableLiveData<ArrayList<CircleItemBean>> = MutableLiveData()
    fun myMangerCircle(searchKeys:String?=null) {
        val circleItemBeans: ArrayList<CircleItemBean> = ArrayList()
        viewModelScope.launch {
            fetchRequest {
                val body = HashMap<String, Any>()
                searchKeys?.apply {
                    body["queryParams"]=HashMap<String,Any>().also {
                        it["searchKeys"]=searchKeys
                    }
                }
                val rkey = getRandomKey()
                apiService.queryMineMangerCircle(body.header(rkey), body.body(rkey))
            }.onSuccess {
                if (null != it && it.size > 0) {
                    it.forEach {
                        it.circles?.let { items ->
                            items[0].typeStr = it.typeStr
                            items[0].isShowTitle = true
                            circleItemBeans.addAll(items)
                        }
                    }
                }
            }
            fetchRequest {
                val body = HashMap<String, Any>()
                searchKeys?.apply {
                    body["queryParams"]=HashMap<String,Any>().also {
                        it["searchKeys"]=searchKeys
                    }
                }
                val rkey = getRandomKey()
                apiService.queryMineMangerOtherCircle(body.header(rkey), body.body(rkey))
            }.onSuccess {
                it?.let {
                    if (it.size > 0) {
                        it[0].typeStr = "处理中"
                        it[0].isShowTitle = true
                    }
                    circleItemBeans.addAll(it)
                }
                mMangerCircle.postValue(circleItemBeans)
            }.onFailure {
                mMangerCircle.postValue(circleItemBeans)
            }
        }
    }

    /**
     * 我加入的圈子
     */
    var mJoinCircle: MutableLiveData<CircleListBean> = MutableLiveData()

    fun myJoinCircle(searchKeys:String?=null) {
        viewModelScope.launch {
            fetchRequest {
                val body = HashMap<String, Any>()
                searchKeys?.apply {
                    body["queryParams"]=HashMap<String,Any>().also {
                        it["searchKeys"]=searchKeys
                    }
                }
                val rkey = getRandomKey()
                apiService.queryMineJoinCircleList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                mJoinCircle.postValue(it)
            }.onFailure {
                mJoinCircle.postValue(null)
            }
        }
    }

    var circleNum: MutableLiveData<CircleUserBean> = MutableLiveData()

    fun queryCircleCount(circleId: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                body["circleId"] = circleId
                var rkey = getRandomKey()
                apiService.queryCircleCount(body.header(rkey), body.body(rkey))
            }.onSuccess {
                circleNum.postValue(it)
            }.onFailure {
                circleNum.postValue(null)
            }
        }
    }


    var circleMember: MutableLiveData<CircleMemberBean> = MutableLiveData()

    /**
     * 加入圈子的
     */
    fun queryJoinCircle(pageNo: Int, circleId: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                body["queryParams"] = mapOf("circleId" to circleId)
                var rkey = getRandomKey()
                apiService.queryJoinCircle(body.header(rkey), body.body(rkey))
            }.onSuccess {
                circleMember.postValue(it)
            }.onFailure {
                circleMember.postValue(null)
            }
        }
    }

    /**
     * 申请加入圈子的
     */
    fun queryJoinCreateCircle(pageNo: Int, circleId: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                body["queryParams"] = mapOf("circleId" to circleId)
                var rkey = getRandomKey()
                apiService.queryJoinCreateCircle(body.header(rkey), body.body(rkey))
            }.onSuccess {
                circleMember.postValue(it)
            }.onFailure {
                circleMember.postValue(it)
            }
        }
    }

    /**
     * 删除圈子成员
     */
    var deleteCircleStatus: MutableLiveData<String> = MutableLiveData()

    fun deleteCircleUsers(circleId: String, userIds: ArrayList<String>) {
        viewModelScope.launch {
            fetchRequest(showLoading = true) {
                var body = HashMap<String, Any>()
                body["circleId"] = circleId
                body["userIds"] = userIds
                var rkey = getRandomKey()
                apiService.deleteCircleUsers(body.header(rkey), body.body(rkey))
            }.onSuccess {
                deleteCircleStatus.postValue("true")
            }.onWithMsgFailure {
                deleteCircleStatus.postValue(it)
            }
        }
    }

    /**
     * 通过审核，不通过审核
     */
    var agreeStatus: MutableLiveData<String> = MutableLiveData()

    fun agree(body: HashMap<String, Any>) {
        viewModelScope.launch {
            fetchRequest {
                //状态 2 同意 3 拒绝
                var rkey = getRandomKey()
                apiService.agreeJoinCircle(body.header(rkey), body.body(rkey))
            }.onSuccess {
                agreeStatus.postValue("true")
            }.onWithMsgFailure {
                agreeStatus.postValue(it)
            }
        }
    }


    fun createCircle(result: (CommonResponse<String>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.createCircle(body.header(rkey), body.body(rkey))
            })
        }
    }

    fun queryCircleStatus(
        circleId: String,
        result: (CommonResponse<ArrayList<CircleStatusItemBean>>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["circleId"] = circleId
                var rkey = getRandomKey()
                apiService.queryCircleStatus(body.header(rkey), body.body(rkey))
            })
        }
    }

    fun setCircleStatus(body: HashMap<String, Any>, result: (CommonResponse<String>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest(showLoading = true) {
                var rkey = getRandomKey()
                apiService.setCircleStatus(body.header(rkey), body.body(rkey))
            })
        }
    }
}