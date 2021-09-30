package com.changanford.common.bean

/**
 *  文件名：CityBean
 *  创建者: zcy
 *  创建日期：2020/5/18 14:36
 *  描述: TODO
 *  修改描述：TODO
 */

class CityBean : ArrayList<CityBeanItem>()

data class CityBeanItem(
    val citys: List<City>,
    val province: Province
)

data class City(
    val city: CityX,
    val district: List<District>
)

data class Province(
    val parentCode: String,
    val regionCode: String,
    val regionId: String,
    val regionName: String,
    val regionType: String
)

data class CityX(
    val parentCode: String,
    val regionCode: String,
    val regionId: String,
    val regionName: String,
    val regionType: String
)

data class District(
    val parentCode: String,
    val regionCode: String,
    val regionId: String,
    val regionName: String,
    val regionType: String
)