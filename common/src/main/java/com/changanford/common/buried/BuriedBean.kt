package com.changanford.common.buried

import com.alibaba.fastjson.JSON
import com.changanford.common.basic.BaseApplication
import com.changanford.common.util.DeviceUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.TimeUtils

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.ccc.bean.BuriedBean
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/27 13:15
 * @Description: 　埋点请求实体
 * *********************************************************************************
 */
class BuriedBean {

    var buriedRecords: ArrayList<BuriedRecord> = ArrayList()

    class BuriedRecord {
        var actName: String = ""// 事件名称
        var actTime: String = ""// 事件触发时间（yyyy-MM-dd HH:ss:mm）
        var actionType: String = ""// 点击页面、查询表单、分享、点赞、留资、查询车型、车型浏览、预约试驾、经销商查询、扫码（自己定义）
        var city: String = ""// 用户所在城市（重庆市、长沙市）
        var clientType: String = ""// 客户端类型小程序(Applet)或APP(APP)
        var country: String = ""// 用户所在国家（中国）
        var deviceId: String = ""// 设备唯一标识,IMEI或IDFA（APP端）
        var enterPageTime: String = ""// 页面访问时间（yyyy-MM-dd HH:ss:mm）
        var extend: String = ""// 扩展字段
        var ip: String = ""// 用户ip,可以不传
        var latitude: String = ""// 纬度
        var longitude: String = ""// 经度
        var mac: String = ""// Mac地址（APP端）
        var openId: String = ""// 微信openId(小程序端)
        var os: String = ""// 操作系统（APP端）
        var pageUrl: String = ""// 页面url
        var phoneNo: String = ""// 手机号码，不含86(小程序端)
        var positionTime: String = ""// 经纬度获取时间（yyyy-MM-dd HH:ss:mm）
        var province: String = ""// 用户所在省份（重庆市、湖南省）
        var requestMethod: String = ""// HTTP请求方法
        var resolution: String = ""// 分辨率，数字X数字
        var responseCode: String = ""// HTTP 响应状态代码
        var sourceUrl: String = ""// 来源页面url
        var ssoId: String = ""// 长安汽车统一认证SSO_ID
        var targetId: String = ""// 用户行为操作对象唯一标识
        var targetName: String = ""// 操作对象名称
        var targetParam: String = ""// 操作参数（比如查询条件，json序列化）
        var terminalBrand: String = ""// 终端品牌（APP端）
        var terminalMode: String = ""// 设备型号（APP端）
        var terminalType: String = ""// 终端类型 手机、 Pad（APP端）
        var unionId: String = ""// 微信unionId(小程序端)
        var userId: String = ""// userId
        var agent: String = ""// 渠道号
        var pageStayTime: String = ""// 停留时长
        var user_id: String = ""// userId

        constructor(
            actName: String,
            actionType: String,
            targetName: String,
            targetId:String,
            userId: String,
            pageStayTime: String,
            extend:String
        ) {
            this.actName = actName
            this.actionType = actionType
            this.targetName = targetName
            this.targetId = targetId
            this.userId = userId
            this.pageStayTime = pageStayTime
            this.extend=extend
            this.user_id=userId
        }
    }

    fun getData(record: BuriedRecord): BuriedBean {

        val time = TimeUtils.InputTimetampAll(System.currentTimeMillis().toString())
        record.actTime = time
        record.city = JSON.parseObject(MConstant.bdLocation)?.let { it.getString("city") }?:""
        record.province = JSON.parseObject(MConstant.bdLocation)?.let { it.getString("province")}?:"" //: String = ""// 用户所在省份（重庆市、湖南省）
        record.latitude = JSON.parseObject(MConstant.bdLocation)?.let { it.getString("latitude")}?:"" //: String = ""// 纬度
        record.longitude = JSON.parseObject(MConstant.bdLocation)?.let { it.getString("longitude")}?:"" //: String = ""// 经度
        record.clientType = "APP"
        record.country = "中国"
        record.deviceId = ""
//            DeviceUtils.getIMEI(BaseApplication.INSTANT)//: String = ""// 设备唯一标识,IMEI或IDFA（APP端）
        record.enterPageTime = time //: String = ""// 页面访问时间（yyyy-MM-dd HH:ss:mm）
//        record.extend = "" //: String = ""// 扩展字段
        record.ip = DeviceUtils.getHostIP() //: String = ""// 用户ip,可以不传
        record.mac = "" //DeviceUtils.getMac() //: String = ""// Mac地址（APP端）
        record.openId = "" //: String = ""// 微信openId(小程序端)
        record.os = "Android" //: String = ""// 操作系统（APP端）
        record.pageUrl = "" //: String = ""// 页面url
        record.phoneNo = "" //: String = ""// 手机号码，不含86(小程序端)
        record.positionTime = time //: String = ""// 经纬度获取时间（yyyy-MM-dd HH:ss:mm）
        record.requestMethod = "POST" //: String = ""// HTTP请求方法
        record.resolution = "" //: String = ""// 分辨率，数字X数字
        record.responseCode = "" //: String = ""// HTTP 响应状态代码
        record.sourceUrl = "" //: String = ""// 来源页面url
        record.ssoId = "" //: String = ""// 长安汽车统一认证SSO_ID
//        record.targetId = "" //: String = ""// 用户行为操作对象唯一标识
        record.targetParam = "" //: String = ""// 操作参数（比如查询条件，json序列化）
        record.terminalBrand = DeviceUtils.getManuFacture() //: String = ""// 终端品牌（APP端）
        record.terminalMode = DeviceUtils.getDeviceModel() //: String = ""// 设备型号（APP端）
        record.terminalType = "手机" //: String = ""// 终端类型 手机、 Pad（APP端）
        record.unionId = "" //: String = ""// 微信unionId(小程序端)
        record.agent = DeviceUtils.getMetaData(BaseApplication.INSTANT,"CHANNEL_VALUE") //: String = ""// 渠道号
        val buriedBean: BuriedBean = BuriedBean()
        buriedBean.buriedRecords = ArrayList()
        buriedBean.buriedRecords.add(record)
        return buriedBean
    }

}