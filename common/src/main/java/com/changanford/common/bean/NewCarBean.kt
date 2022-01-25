package com.changanford.common.bean

/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : NewCarBean
 */
data class NewCarInfoBean(
    val id:String?=null,
    val pic: String?=null,
    val canNotSeeCarType: String? = null,
    val canNotUseCarType: String? = null,
    val canSeeAuth: Int = 0,
    val carOwnerSort: Int = 0,
    val icons: List<NewCarTagBean>? = null,
    val modelCode: String = "",
    val modelName: String = "",
    val searchValue: Any? = null,
    val isAuth:Any?=null,
){
    //该模块是否可见 查询存在即为不可见
    fun isVisible(carModelCode:String):Boolean{
        return  if(canNotSeeCarType!=null&&!canNotSeeCarType.contains(",")){
            carModelCode!=canNotSeeCarType
        }else{
            canNotSeeCarType?.split(",")?.find { carModelCode== it }==null
        }
    }
    //是否可用
    fun isUse(carModelCode:String):Boolean{
        return  if(canNotUseCarType!=null&&!canNotUseCarType.contains(",")){
            carModelCode!=canNotUseCarType
        }else{
            canNotUseCarType?.split(",")?.find { carModelCode== it }==null
        }
    }
}

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
data class NewCarTagBean(
    val tagId:String?=null,
    val tagName:String?=null,
    val pic:String?=null,
    val iconId: Int = 0,
    val iconImg: String? = null,
    val iconName: String? = null,
    val jumpDataType: Int = 0,
    val jumpDataValue: String? = null,
    val myCarModelId: Int = 0,
)