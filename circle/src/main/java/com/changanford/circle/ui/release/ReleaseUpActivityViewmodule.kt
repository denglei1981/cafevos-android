package com.changanford.circle.ui.release

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.QueryDetail
import com.changanford.common.bean.QueryInfo
import com.changanford.common.bean.STSBean
import com.changanford.common.net.*
import kotlinx.coroutines.launch

class ReleaseUpActivityViewmodule  : BaseViewModel() {
    @JvmField
    var queryDetail = MutableLiveData<QueryDetail>();

    /**
     * 提交问卷
     */
    fun CommitQuery(queryInfo: QueryInfo, rpo : (CommonResponse<Any>)->Unit) {
        viewModelScope.launch {
            rpo(fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                body["queryInfo"] = queryInfo
                apiService.addQuery(body.header(rkey),body.body(rkey))
            })
        }
    }

    /**
     * 修改问卷
     */
    fun UPdatQuery(wonderfulId:Int, queryInfo: QueryInfo, rpo : (CommonResponse<Any>)->Unit) {
        viewModelScope.launch {
            rpo(fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                body["queryInfo"] = queryInfo
                body["wonderfulId"] =wonderfulId
                apiService.UPdatQuery(body.header(rkey),body.body(rkey))
            })
        }

    }


    /**
     * 调查详情
     */
    fun Querydetail(wonderfulId:Int) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                body["wonderfulId"] =wonderfulId
                body["dataType"] =0
                apiService.queryDetail(body.header(rkey),body.body(rkey))
            }.onSuccess {
                if (it!=null){
                    queryDetail.value = it
                }
            }
        }

    }


    fun GetOSS(rpo : (CommonResponse<STSBean>)->Unit) {
        viewModelScope.launch {
            var body = HashMap<String, Any>()
            var rkey = getRandomKey()
            rpo(fetchRequest {
                apiService.getOSS(body.header(rkey), body.body(rkey))
            })
        }
    }

}