package com.changanford.common.bean

import android.text.TextUtils
import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : NewCarBean
 */
data class NewCarInfoBean(
    val id: String? = null,
    val pic: String? = null,
    val canNotSeeCarType: String? = null,
    val canNotUseCarType: String? = null,
    val canSeeAuth: Int = 0,
    val carOwnerSort: Int = 0,
    val icons: List<NewCarTagBean>? = null,
    val modelCode: String = "",
    val modelName: String = "",
    val searchValue: Any? = null,
    val isAuth: Any? = null,
    val phone: String? = null,
    val mainOnePic: String? = null,
    val dealerName: String? = null,
    val address: String? = null,
    val dealerId: String? = null,
    val dealerCode: String? = null,
    val groupName: String? = null,
    val groupCode: String? = null,
    val distanct: String? = null,
    val jumpDataType: Int? = null,
    val jumpDataValue: String? = null,
    val lngX: String? = null,
    val latY: String? = null,
    var modelSort: Int? = null,
) {
    //该模块是否可见 查询存在即为不可见
    fun isVisible(carModelCode: String): Boolean {
        return if (TextUtils.isEmpty(canNotSeeCarType)) true
        else if (canNotSeeCarType != null && !canNotSeeCarType.contains(",")) {
            carModelCode != canNotSeeCarType
        } else {
            canNotSeeCarType?.split(",")?.find { carModelCode == it } == null
        }
    }

    //是否可用
    fun isUse(carModelCode: String): Boolean {
        return if (TextUtils.isEmpty(canNotUseCarType)) true
        else if (canNotUseCarType != null && !canNotUseCarType.contains(",")) {
            carModelCode != canNotUseCarType
        } else {
            canNotUseCarType?.split(",")?.find { carModelCode == it } == null
        }
    }
}

data class NewCarBannerBean(
    val bottomAni: Int? = null,
    val bottomImg: String? = null,
    val bottomJumpType: Int? = null,
    val bottomJumpVal: String? = null,
    val carModelCode: String = "",
    val carModelId: Int = 0,
    val carModelName: String? = null,
    val id: Int = 0,
    val mainImg: String? = null,
    val mainIsVideo: Int = 0,
    val mainJumpType: Int? = null,
    val mainJumpVal: String? = null,
    val name: String = "",
    val sort: Int = 0,
    val status: Int = 0,
    val topAni: Int? = null,
    val topImg: String? = null,
    val topJumpType: Int? = null,
    val topJumpVal: String? = null,
    var maPlanId: String? = "",
    var maJourneyActCtrlId: String? = "",
    var maJourneyId: String? = "",
)

data class NewCarTagBean(
    val tagId: String? = null,
    val tagName: String? = null,
    val pic: String? = null,
    val iconId: Int = 0,
    val iconImg: String? = null,
    val iconName: String? = null,
    val jumpDataType: Int = 0,
    val jumpDataValue: String? = null,
    val myCarModelId: Int = 0,
    val spuId: String? = null,
    var spuCode: String? = null,//车型编码
    var spuName: String? = null,//车型编码
    var carLbName: String? = null,//车型编码
    var carModelPic: String? = null,//车型名称
    var url: String? = null, override val itemType: Int = 0,
) : MultiItemEntity

data class CarMoreInfoBean(
    var carModelMoreJump: JumpDataBean,
    var carModels: ArrayList<NewCarTagBean>,
    var carInfos: ArrayList<NewCarTagBean>
)

data class DistanceBean(var zoom: Float, var distance: Int)