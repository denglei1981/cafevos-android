package com.changanford.common.bean

/**
 *  文件名：IndustryBean
 *  创建者: zcy
 *  创建日期：2020/5/22 18:24
 *  描述: TODO
 *  修改描述：TODO
 */


data class IndustryBeanItem(
    val industryIcon: String,
    val industryId: Int,
    val industryName: String,
    val list: List<IndustryItemBean>
)

data class IndustryItemBean(
    val createTime: Long,
    val industryIcon: String,
    val industryId: Int,
    val industryName: String,
    val parentIndustryId: Int,
    val status: Int
)

//行业选择回调
data class IndustryReturnBean(var ids: String, var names: String)