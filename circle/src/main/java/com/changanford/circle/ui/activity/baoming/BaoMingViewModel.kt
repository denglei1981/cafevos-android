package com.changanford.circle.ui.activity.baoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.circle.bean.BaoMingReqBean
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.AttributeBean
import com.changanford.common.chat.utils.LogUtil
import com.changanford.common.net.*
import kotlinx.coroutines.launch

class BaoMingViewModel : BaseViewModel() {

    var baomingReqBean: LiveData<BaoMingReqBean> = MutableLiveData(BaoMingReqBean())

    var attributeBean = MutableLiveData<AttributeBean>()

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
}