package com.changanford.circle.bean

import com.changanford.common.bean.HomeExtendBean

/**
 *Author lcw
 *Time on 2021/9/28
 *Purpose
 */
data class HomeDataListBean<DATA>(
    val dataList: ArrayList<DATA>,
    val extend: HomeExtendBean,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)