package com.changanford.common.util

import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import com.alibaba.fastjson.JSON
import com.changanford.common.bean.CarItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath

/**
 *  文件名：CommonUtils
 *  创建者: zcy
 *  创建日期：2021/2/19 10:58
 *  描述: incall融合，状态判断
 *  修改描述：TODO
 */
object CommonUtils {

    fun isInCallSuccess(realnameAuthStatus: String): Boolean {
        return realnameAuthStatus == "AUTHED"
    }

    fun isInCallFail(realnameAuthStatus: String): Boolean {
        return realnameAuthStatus == "AUTH_FAILED"
    }

    fun isInCallStatusIng(realnameAuthStatus: String): Boolean {
        return realnameAuthStatus == "AUTHING"
    }

    fun isCrmSuccess(status: Int): Boolean {
        return status == 5
    }

    fun isCrmFail(status: Int): Boolean {
        return status == 3 || status == 4
    }

    fun isCrmStatusIng(status: Int): Boolean {
        return status == 1 || status == 2
    }

    /**
     * 跳转incall详情
     */
    fun skipInCallInfo(item: CarItemBean, carManage: Boolean) {
        var map = mapOf<String, Any>(
            "isIncall" to isInCallSuccess(item.realnameAuthStatus),
            "vin" to item.vin,
            "carId" to item.id.toString(),
            "carManage" to carManage,
            "targetPage" to when {
                isInCallStatusIng(item.realnameAuthStatus) -> {
                    "checking"
                }
                isInCallFail(item.realnameAuthStatus) -> {
                    "checkFail"
                }
                else -> {
                    ""
                }
            },
            "crmAuthState" to when (item.status) {
                5 -> { //认证成功
                    1
                }
                1, 2 -> {//认证中
                    2
                }
                else -> {//其他状态
                    0
                }
            }
        )
        JumpUtils.instans?.jump(56, JSON.toJSONString(map))
    }

    /**
     * 跳转uni购卡页面
     *  value：vin
     *  uniCardId：进入购买页面选择的uni卡id(uniCardId)，升级时传需要升级的uni卡id(bestUniCardId)
     *  type:区别车辆类型，=1 输入vin进入下单页面(没有认证车辆)，=2 车辆认证列表进入下单页面（有认证车辆，可选择车辆购买）
     *  isDealer：是否选择专属经销商，true选择专属经销商，false
     *  uniDistributorShareId：  shareId，
     *  seriesCode：车系，uni-t(S202) uni-k
     */
    fun startUniCarPayOrder(
        vin: String?,
        uniCardId: String?,
        type: Int,
        isDealer: Boolean = false,
        uniDistributorShareId: String?,
        seriesCode: String = ""
    ) {
        var bundle: Bundle = Bundle()
        bundle.putString("value", vin)
        bundle.putString("uniCardId", uniCardId)
        bundle.putInt("type", type)//区别是否有认证车辆
        bundle.putBoolean("isDealer", isDealer)
        bundle.putString("uniDistributorShareId", uniDistributorShareId)
        bundle.putString("seriesCode", seriesCode)
        RouterManger.startARouter(ARouterMyPath.UniCardPayOrderUI, bundle)
    }


    /**
     * 活动跳转
     */
    fun jumpActDetail(jumpType: Int, jumpVal: String? = "") {
        //跳转类型(1跳转外部，2跳转内部，3常规)
        when (jumpType) {
            1 -> {
                JumpUtils.instans?.jump(
                    10000,
                    jumpVal
                )
            }
            2, 3 -> {
                JumpUtils.instans?.jump(
                    1,
                    jumpVal
                )
            }
            else -> {
                JumpUtils.instans?.jump(
                    jumpType,
                    jumpVal
                )
            }
        }
    }

}

/**
 * 活动类型
 * //精彩类型，0-线上活动，1-线下活动，2-问卷
 */
fun AppCompatTextView.actTypeText(wonderfulType: Int? = 0) {
    text = when (wonderfulType) {
        0 -> {
            "线上活动"
        }
        1 -> {
            "线下活动"
        }
        2 -> {
            "调查问卷"
        }
        3 -> {
            "福域活动"
        }
        else -> {
            "福域活动"
        }
    }
}

/**
 * 查询认证车辆，筛选数据状态
 */
enum class AuthCarStatus {
    ALL, INCALL_SUCCESS, CRM_SUCCESS, INCALL_CRM_SUCCESS
}