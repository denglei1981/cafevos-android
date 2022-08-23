package com.changanford.circle.ui.activity.baoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.circle.bean.BaoMingReqBean
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.AttributeBean
import com.changanford.common.bean.DtoBean
import com.changanford.common.bean.DtoBeanNew
import com.changanford.common.chat.utils.LogUtil
import com.changanford.common.net.*
import kotlinx.coroutines.launch

class BaoMingViewModel : BaseViewModel() {

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

    /**
     * 发布活动
     */
    fun CommitACT(dtoBean: DtoBeanNew, rpo: (CommonResponse<Any>) -> Unit) {
        viewModelScope.launch {
            rpo(fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                body["coverImgUrl"] = dtoBean.coverImgUrl
                body["activityAddr"] = dtoBean.activityAddr
                body["attributes"] = dtoBean.attributes
                body["beginTime"] = dtoBean.beginTime
                body["cityId"] = dtoBean.cityId
                body["cityName"] = dtoBean.cityName
                body["content"] = dtoBean.content
                body["contentImgList"] = dtoBean.contentImgList
                body["endTime"] = dtoBean.endTime
                body["latitude"] = dtoBean.latitude
                body["longitude"] = dtoBean.longitude
                body["provinceId"] = dtoBean.provinceId
                body["provinceName"] = dtoBean.provinceName
                body["signBeginTime"] = dtoBean.signBeginTime
                body["signEndTime"] = dtoBean.signEndTime
                body["title"] = dtoBean.title
                body["townName"] = dtoBean.townName
                body["wonderfulType"] = dtoBean.wonderfulType
                apiService.ADDActNew(body.header(rkey), body.body(rkey))
            })
        }
    }
}