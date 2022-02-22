package com.changanford.common.bean

/**
 *  文件名：RootTaskBean
 *  创建者: zcy
 *  创建日期：2021/9/13 15:41
 *  描述: TODO
 *  修改描述：TODO
 */
data class RootTaskBean(
    val list: ArrayList<ItemTaskBean>,
    val taskTypeName: String,
    val taskType: Int = 1,
    val userTatalScore: Double = 0.0
)

data class ItemTaskBean(
    val jumpDataType: Int,
    val jumpDataValue: String,
    val taskAllCount: Int,
    val taskBrief: String,
    val taskDoneCount: Int,
    val taskGrowthValue: Int,
    val taskIcon: String,
    val taskIsDone: Int,
    val taskIsOpen: Int,
    val taskName: String,
    val taskScore: Int,
    val taskSort: Int,
    val taskType: Int,
    val taskAbcCount: Int
)

data class GrowUpBean(
    val dataList: List<GrowUpItem>,
    val extend: GrowUpExtend,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class GrowUpItem(
    val actionId: Int,
    val actionName: String,
    val businessId: String,
    val createBy: String,
    val createTime: Long,
    val desc: String,
    val growth: Int,
    val growthDecimal: Double,
    val growthlSum: String,
    val integral: Int,
    val integralDecimal: Double,
    val integralSum: String,
    val logId: Int,
    val remark: String,
    val searchValue: String,
    val status: Int,
    val updateBy: String,
    val updateTime: String,
    val userId: Int,
    val userName: String,
    val source: String = "福域" //来源
)

data class GrowUpQYBean(
    val interestsId: Int,
    val interestsCode: String,
    val interestsName: String,
    val icon: String
)

data class GrowUpExtend(
    val growSeriesName: String,
    val growthSum: Double,
    val integralSum: Double,
    val nextGrowSeriesName: String,
    val nextSeriesMinGrow: Int,
    val multiple: String?,
    val rulesDesc: String,
    val historyDesc: String,
    val source: String,
    val growSeriesMaxGrow: String
)
