package com.changanford.common.receiver

import android.os.Bundle
import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.sdk.android.push.AndroidPopupActivity
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouterFinish
import com.changanford.common.util.MConstant

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.receiver.MyMessageHelpReceiver
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/23 15:35
 * @Description: 　
 * *********************************************************************************
 */
class MyMessageHelpReceiver : AndroidPopupActivity() {

    override fun onSysNoticeOpened(p0: String?, p1: String?, p2: MutableMap<String, String>?) {
        Log.i(MyMessageReceiver.REC_TAG, "onNotificationClickedWithNoAction ： " + " : ")
        if (p2 != null && !p2.isEmpty()) {
            try {
                val jsonObject = JSON.parseObject(JSON.toJSONString(p2))
                val jumpDataType = jsonObject.getString("jumpDataType")
                val jumpDataValue = jsonObject.getString("jumpDataValue")
//                instans!!.jump(
//                    Integer.valueOf(jumpDataType),
//                    jumpDataValue
//                )
                var bundle = Bundle()
                bundle.putString("jumpDataType",jumpDataType)
                bundle.putString("jumpDataValue",jumpDataValue)
                startARouterFinish(this, ARouterHomePath.SplashActivity,bundle)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(MyMessageReceiver.REC_TAG, "数据解析错误")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (MConstant.isAppAlive) {
            finish()
        }
    }
}