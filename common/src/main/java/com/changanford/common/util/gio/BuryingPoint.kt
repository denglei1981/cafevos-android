package com.changanford.common.util.gio

import com.changanford.common.bean.GioPreBean
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.growingio.android.sdk.autotrack.GrowingAutotracker

/**
 *Author lcw
 *Time on 2023/1/17
 *Purpose
 */

fun trackCustomEvent(type: String, map: HashMap<String, String>) {
    GrowingAutotracker.get().trackCustomEvent(type, map)
}

fun setTrackCmcUserId(userId:String){
    GrowingAutotracker.get().setLoginUserId(userId)
}

fun updatePersonalData(prePageName: String, prePageType: String) {
    LiveDataBus.get().with(LiveDataBusKey.UPDATE_PERSONAL_GIO)
        .postValue(GioPreBean(prePageName = prePageName, prePageType = prePageType))
}

fun updateCircleDetailsData(prePageName: String, prePageType: String) {
    LiveDataBus.get().with(LiveDataBusKey.UPDATE_CIRCLE_DETAILS_GIO)
        .postValue(GioPreBean(prePageName = prePageName, prePageType = prePageType))
}

fun updateGoodsDetails(prePageName: String, prePageType: String) {
    LiveDataBus.get().with(LiveDataBusKey.UPDATE_GOODS_DETAILS_GIO)
        .postValue(GioPreBean(prePageName = prePageName, prePageType = prePageType))
}

fun updateTaskList(prePageName: String, prePageType: String) {
    LiveDataBus.get().with(LiveDataBusKey.UPDATE_TASK_LIST_GIO)
        .postValue(GioPreBean(prePageName = prePageName, prePageType = prePageType))
}

fun updateMainGio(prePageName: String, prePageType: String) {
    LiveDataBus.get().with(LiveDataBusKey.UPDATE_MAIN_GIO)
        .postValue(GioPreBean(prePageName = prePageName, prePageType = prePageType))
}

fun updateInfoDetailGio(prePageName: String, prePageType: String) {
    LiveDataBus.get().with(LiveDataBusKey.UPDATE_INFO_DETAIL_GIO)
        .postValue(GioPreBean(prePageName = prePageName, prePageType = prePageType))
}