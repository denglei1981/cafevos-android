package com.changanford.home.bean

import com.changanford.common.bean.HomeExtendBean
import com.changanford.common.bean.InfoDataBean


class NewsListMainBean(
    var dataList: ArrayList<InfoDataBean>,
    val extend: HomeExtendBean,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)



