package com.changanford.circle.ui.release

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.ActivityBean
import com.changanford.common.bean.AttributeBean
import com.changanford.common.bean.DtoBean
import com.changanford.common.bean.STSBean
import com.changanford.common.chat.utils.LogUtil
import com.changanford.common.net.*
import kotlinx.coroutines.launch

class ReleaseActivityViewModel() : BaseViewModel() {
    @JvmField
    var attributeBean = MutableLiveData<AttributeBean>()

    @JvmField
    var activityBean = MutableLiveData<ActivityBean>()


    fun getAttributes() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                body["Content-Type"] = "activiity_list_topad"
                apiService.getAttributes(body.header(rkey), body.body(rkey))
            }.onSuccess {
                LogUtil.d(it?.toString())
                attributeBean.value = it
            }
        }
    }

    /**
     * 发布活动
     */
    fun CommitACT(dtoBean: DtoBean, rpo: (CommonResponse<Any>) -> Unit) {
        viewModelScope.launch {
            rpo(fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                body["dto"] = dtoBean
                apiService.ADDAct(body.header(rkey), body.body(rkey))
            })
        }
    }

    fun GetOSS(rpo: (CommonResponse<STSBean>) -> Unit) {
        viewModelScope.launch {
            var body = HashMap<String, Any>()
            var rkey = getRandomKey()
            rpo(fetchRequest {
                apiService.getOSS(body.header(rkey), body.body(rkey))
            })
        }
    }


    fun getActDetail(wonderid: Int) {
        viewModelScope.launch {
            var body = HashMap<String, Any>()
            body["wonderfulId"] = wonderid
            var rkey = getRandomKey()
            fetchRequest {
                apiService.getActDetail(body.header(rkey), body.body(rkey))
            }.onSuccess {
                activityBean.value = it
            }
        }
    }


    /**
     * 修改活动
     */
    fun UpdateACT(wonderfulId: Int, dtoBean: DtoBean, rpo: (CommonResponse<Any>) -> Unit) {
        var body = HashMap<String, Any>()
        var rkey = getRandomKey()
        body["dto"] = dtoBean
        body["wonderfulId"] = wonderfulId
        viewModelScope.launch {
            rpo(fetchRequest {
                apiService.UPdateAct(body.header(rkey), body.body(rkey))
            })
        }
    }

}