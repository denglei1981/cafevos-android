package com.changanford.common.bean

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.ccc.bean.UpdateInfo
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/6/12 13:50
 * @Description: 　
 * *********************************************************************************
 */
class UpdateInfo {
    var id : String? = null //:3
    var versionNumber : String? = null //:"3.0.2",
    var versionName : String? = null //:"第一版",
    var versionContent : String? = null //:"是的",
    var downloadUrl : String? = null //:"第一版",
    var isForceUpdate : Int? = null //:1, 0 不强制更新 1 强制更新
    var weight : Int? = null //:12,
    var appType : Int? = null //:0,
    var createTime : String? = null //:1590220141000
}