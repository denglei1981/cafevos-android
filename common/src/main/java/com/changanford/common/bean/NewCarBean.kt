package com.changanford.common.bean

/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : NewCarBean
 */
data class NewCarBean(val id:String?=null)

data class NewCarBannerBean(
    val bottomAni: Int? = 1,
    val bottomImg: String = "",
    val bottomJumpType: Int? = null,
    val bottomJumpVal: String? = null,
    val carModelCode: String = "",
    val carModelId: Int = 0,
    val carModelName: String? = null,
    val id: Int = 0,
    val mainImg: String = "",
    val mainIsVideo: Int = 0,
    val mainJumpType: Int? = null,
    val mainJumpVal: String? = null,
    val name: String = "",
    val sort: Int = 0,
    val status: Int = 0,
    val topAni: Int = 1,
    val topImg: String = "",
    val topJumpType: Int? = null,
    val topJumpVal: String? = null,
)
data class NewCarTagBean(val tagId:String?=null,val tagName:String?=null,val pic:String?=null)
