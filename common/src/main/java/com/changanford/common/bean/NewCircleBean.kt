package com.changanford.common.bean

/**
 * @Author : wenke
 * @Time : 2022/1/5 0005
 * @Description :圈子
 */
data class CirceHomeBean(val circleTypes:ArrayList<NewCircleBean>?=null,
                         val myCircles:ArrayList<NewCircleBean>?=null,
                         val circlePK:ArrayList<NewCircleBean>?=null,
                         val topList:ArrayList<CirCleHotList>?=null,
                         val dataList:ArrayList<NewCircleBean>?=null)
data class CirCleHotList(val topId:Int=0,val topName:String?=null,val circleTops:ArrayList<NewCircleBean>?=null)

data class TagInfoBean(val tagMaxCount:Int?=0,var tags:List<NewCirceTagBean>?=null)
data class NewCirceTagBean(var id:String?=null,var isCheck:Boolean?=false,var tagName:String?=null,var tagId:Int?=null,val icon:String?=null)

data class NewCircleDataBean(
    val dataList:ArrayList<NewCircleBean>?=null,
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)
data class NewCircleBean(
    val id:String?=null,
    val name: String?=null,
    val icon:String?=null,
    val isRegion:String?=null,
    val isRecommend:String?=null,
    val openClose:String?=null,
    val dataState:String?=null,
    val checkNoReason: String = "",
    val checkPassTime: Long = 0,
    val checkStatus: Int = 0,
    val circleId: String? = "0",
    val cityId: Any? = null,
    val cityName: String? = null,
    val createBy: Any? = null,
    val createTime: Long = 0,
    val description: String? = null,
    val districtId: Any? = null,
    val districtName: Any? = null,
    val hotIcon: Any? = null,
    val isGrounding: Int = 0,
    val isHot: Int = 0,
    val isViewManagerBtn: Any? = null,
    val lastPostsLikeTime: Any? = null,
    val lastPostsTime: Long = 0,
    val lat: Any? = null,
    val lng: Any? = null,
    val maxUserCount: Int = 0,
    val nameColor: Any? = null,
    val nameLike: Any? = null,
    val pic: String? = null,
    val postsCount: Int = 0,
    val provinceId: Any? = null,
    val provinceName: Any? = null,
    val remark: Any? = null,
    val searchValue: Any? = null,
    val sortOrder: Int = 0,
    val topId: Int = 0,
    val type: Any? = null,
    val updateBy: Any? = null,
    val updateTime: Long = 0,
    val userCount: String? = "0",
    val userId: String = "0",
    val tags:ArrayList<NewCirceTagBean>?=null,
)
