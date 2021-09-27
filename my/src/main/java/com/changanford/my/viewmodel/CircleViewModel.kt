package com.changanford.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.CircleListBean
import com.changanford.common.bean.CircleMangerBean
import com.changanford.common.bean.CircleMemberBean
import com.changanford.common.bean.CircleUserBean
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
    var mMangerCircle: MutableLiveData<CircleMangerBean> = MutableLiveData()

    fun myMangerCircle() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.queryMineMangerCircle(body.header(rkey), body.body(rkey))
            }.onSuccess {
                if (null != it && it.size > 0) {
                    mMangerCircle.postValue(it[0])
                }
            }.onFailure {
                mMangerCircle.postValue(null)
            }
        }
    }

    /**
     * 我加入的圈子
     */
    var mJoinCircle: MutableLiveData<CircleListBean> = MutableLiveData()

    fun myJoinCircle() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
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
}