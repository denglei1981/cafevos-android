package com.changanford.my.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.view.isVisible
import com.alibaba.fastjson.JSON
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CarItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.CommonUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.my.R
import com.changanford.my.databinding.ItemCarAuthBinding

/**
 *  文件名：CarAuthHolder
 *  创建者: zcy
 *  创建日期：2021/9/22 16:56
 *  描述: TODO
 *  修改描述：TODO
 */
fun CarAuthHolder(
    holder: BaseDataBindingHolder<ItemCarAuthBinding>,
    item: CarItemBean,
) {
    holder.dataBinding?.let {
        it.tvVin.text = "VIN码：${item.vin}"
        it.tvCarName.text =
            if (item.seriesName.isNullOrEmpty()) item.modelName else item.seriesName
        //设置后台配置的车系图片
        if (item.modelUrl.isNullOrEmpty()) {
            it.carPic.setImageResource(R.mipmap.ic_car_auth_ex)
        } else {
            it.carPic.load(item.modelUrl, R.mipmap.ic_car_auth_ex)
        }
        var d = it.tvAuth.background as GradientDrawable
//        d.setColor(Color.parseColor("#60000000"))
//        item.authStatus = 3
        when {
            isCrmStatusIng(item) -> {
                it.tvAuth.text = if (item.authStatus == 2) "审核中" else "认证中"
                crmHint(1, it, item)
            }
            isCrmFail(item) -> {
                it.tvAuth.text = "审核未通过"
                crmHint(2, it, item)
            }
            isCrmSuccess(item) -> {
                if (item.isDefault == 1) {
                    it.tvAuth.text = "已认证"
                    it.tvDefault.isVisible = true
                }else{
                    it.tvAuth.text = "已认证"
                }
//                d.setColor(Color.parseColor("#691700f4"))
                crmHint(3, it, item)
            }
            else -> {
                it.tvAuth.text = "未认证"
                crmHint(3, it, item)
            }
        }
        holder.itemView.setOnClickListener { _ ->
            skipCrmCarInfo(item)
//            RouterManger.param("value", item.vin)
//                .param("plateNum", item.plateNum ?: "")
//                .startARouter(ARouterMyPath.AddCardNumTransparentUI)
        }
    }
}

/**
 * 跳转InCall详情
 */
fun skipInCallCarInfo(item: CarItemBean) {
    CommonUtils.skipInCallInfo(item, true)
}

/**
 * 跳转crm详情
 */
fun skipCrmCarInfo(item: CarItemBean) {
    JumpUtils.instans?.jump(
        50,
        JSON.toJSONString(
            mapOf(
                "vin" to item.vin,
                "status" to item.authStatus,
                "isNeedChangeBind" to item.isNeedChangeBind,
                "authId" to item.authId,
                "carSalesInfoId" to item.carSalesInfoId
            )
        )
    )
}

/**
 * 列表crm提示
 */
private fun crmHint(
    hintStats: Int,
    holder: ItemCarAuthBinding,
    item: CarItemBean
) {
    holder.btnAddCarNum.visibility = View.GONE
    holder.authReason.visibility = View.GONE
    when (hintStats) {
        1, 2 -> {//审核中,认证失败
            holder.authReason.visibility = View.VISIBLE
            holder.authReason.text = "请等待审核，审核时间为1-3个工作日"
            if (hintStats == 2) {//认证失败
                holder.authReason.text = "原因：${item.examineRemakeFront ?: ""}"
            }
        }
        3 -> {//CRM认证成功
            holder.btnAddCarNum.visibility = View.VISIBLE
            if (item.plateNum.isNullOrEmpty() || "无牌照" == item.plateNum) {
                holder.btnAddCarNum.apply {
                    text = "添加车牌"
                    setBackgroundResource(R.drawable.shape_car_auth_btn_bg)
                    setTextColor(Color.parseColor("#1700f4"))
                }
            } else {
                holder.btnAddCarNum.apply {
                    setBackgroundResource(R.drawable.shape_car_num_btn_bg)
                    setTextColor(Color.parseColor("#1700f4"))
                    text = "${item.plateNum}"
                }
            }
            holder.btnAddCarNum.setOnClickListener {
                RouterManger.param("value", item.carSalesInfoId)
                    .param("plateNum", item.plateNum ?: "")
                    .param("authId",item.authId)
                    .param("carSalesInfoId",item.carSalesInfoId)
                    .startARouter(ARouterMyPath.AddCardNumTransparentUI)
            }
        }
    }
}


//AUTHED   认证通过
//AUTHING  认证中
//AUTH_FAILED 认证失败
//CHECK_FAILED 审核失败
//CHECK_PICFAILED 审核失败
//CHECK_SUCCESS 审核通过
//CHECK_WAIT 审核中
//UNAUTH 未实名  @蒋小寒 @周春宇 

//AUTHED:认证通过
//AUTHING:认证中
//AUTH_FAILED:认证失败
//UNAUTH：未实名"
fun isInCallSuccess(item: CarItemBean): Boolean {
    return CommonUtils.isInCallSuccess(item.realnameAuthStatus)
}

fun isInCallFail(item: CarItemBean): Boolean {
    return CommonUtils.isInCallFail(item.realnameAuthStatus)
}

fun isInCallStatusIng(item: CarItemBean): Boolean {
    return CommonUtils.isInCallStatusIng(item.realnameAuthStatus)
}

fun isCrmSuccess(item: CarItemBean): Boolean {
    return CommonUtils.isCrmSuccess(item.authStatus)
}

fun isCrmFail(item: CarItemBean): Boolean {
    return CommonUtils.isCrmFail(item.authStatus)
}

fun isCrmStatusIng(item: CarItemBean): Boolean {
    return CommonUtils.isCrmStatusIng(item.authStatus)
}

fun isCrmStatusNotBind(item: CarItemBean): Boolean {
    return CommonUtils.isCrmNotBind(item.authStatus)
}

/**
 * 跳转incall审核中页面
 */
fun toInCallChecking(bean: CarItemBean) {
    CommonUtils.skipInCallInfo(bean, false)
}

/**
 * 跳转icall失败页面
 */
fun toInCallFail(bean: CarItemBean) {
    CommonUtils.skipInCallInfo(bean, false)
}