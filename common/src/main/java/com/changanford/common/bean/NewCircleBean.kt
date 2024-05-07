package com.changanford.common.bean

import java.io.Serializable

/**
 * @Author : wenke
 * @Time : 2022/1/5 0005
 * @Description :圈子
 */
data class CirceHomeBean(
    val circleTypes: ArrayList<NewCircleBean>? = null,
    val myCircles: ArrayList<NewCircleBean>? = null,
    val circlePK: ArrayList<NewCircleBean>? = null,
    val topList: ArrayList<CirCleHotList>? = null,
    val dataList: ArrayList<NewCircleBean>? = null,
    //加入圈子数量
    val joinCircleNum:Int,
    //未加入圈子数量
    val noJoinCircleNum:Int,
    //未加入圈子封面,
    val noJoinCirclePics:ArrayList<String>
)

data class CirCleHotList(
    val topId: Int = 0,
    val topName: String? = null,
    val circleTops: ArrayList<NewCircleBean>? = null
)

data class TagInfoBean(
    val tagMaxCount: Int? = 0,
    var tags: List<NewCirceTagBean>? = null,
    var circleTypes: List<NewCirceTagBean>? = null
)

data class NewCirceTagBean(
    var id: String? = null,
    var isCheck: Boolean? = false,
    var tagName: String? = null,
    var tagId: Int? = null,
    val icon: String? = null,
    val name: String? = null,
    val operator: String? = null,
)

data class NewCircleDataBean(
    val dataList: ArrayList<NewCircleBean>,
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class NewCircleBean(
    val id: String? = null,
    val name: String = "",
    val icon: String? = null,
    val addrDesc: String? = null,
    val isRegion: String? = null,
    //圈子认证 0 未认证 1已认证
    val manualAuth: Int = 0,
    //圈子认证V标
    val manualAuthImg: String? = null,
    val isRecommend: String? = null,
    val openClose: String? = null,
    val dataState: String? = null,
    val checkNoReason: String = "",
    val checkPassTime: Long = 0,
    val checkStatus: Int = 0,
    val circleId: String = "0",
    val cityId: Any? = null,
    val cityName: String? = null,
    val createBy: Any? = null,
    val createTime: Long = 0,
    val description: String = "",
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
    val tags: ArrayList<NewCirceTagBean>? = null,
    var avatars: ArrayList<String>? = arrayListOf(),
    var star: String? = null,
    var isApply: Int?=0,
    var isJoin:String?=null,//是否加入圈子 未加入（TOJOIN）、待审核（PENDING）、已加入（JOINED）
)

data class SerachUserAddress(
    val addrName: String,
    val address: String,
    val city: String,
    val createTime: String,
    val demo: String,
    val district: String,
    val lat: Double,
    val lon: Double,
    val postsAddrId: Long,
    val province: String,
)


data class Topic(
    val description: String = "",
    val likesCount: Int = 0,
    val name: String = "",
    val heat: String = "",
    val pic: String = "",
    val circleId: String = "",
    val postsCount: Int = 0,
    val isGrounding: Int = 0,
    val topicId: Int = 0,
    val userCount: Int = 0,
    val viewsCount: Int = 0,
    var isHot: Int = 0,
    var isNew: String = "",
    var reason: String = "",
    var checkStatus: String = "",
) : Serializable

