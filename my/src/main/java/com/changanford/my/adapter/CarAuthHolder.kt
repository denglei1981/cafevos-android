package com.changanford.my.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CarItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.CommonUtils
import com.changanford.common.utilext.load
import com.changanford.common.utilext.logE
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
            if (item.carName.isNullOrEmpty()) item.modelName else item.carName
        //设置后台配置的车系图片
        if (item.modelUrl.isNullOrEmpty()) {
            //                    it.carPic.setImageResource(R.mipmap.uni_car_sample)
        } else {
            it.carPic.load(item.modelUrl, R.mipmap.ic_car_auth_ex)
        }
        var d = it.tvAuth.background as GradientDrawable
        d.setColor(Color.parseColor("#60000000"))
        when {
            isCrmStatusIng(item) -> {
                it.tvAuth.text = "认证中"
                crmHint(1, it, item)
            }
            isCrmFail(item) -> {
                it.tvAuth.text = "审核不通过"
                crmHint(2, it, item)
            }
            isCrmSuccess(item) -> {
                it.tvAuth.text = "已认证"
                d.setColor(Color.parseColor("#00095B"))
                crmHint(3, it, item)
            }
            else -> {
                it.tvAuth.text = "未认证"
                crmHint(3, it, item)
            }
        }
        holder.itemView.setOnClickListener { _ ->
            when {
                isCrmSuccess(item) -> {//成功跳详情
                    skipCrmCarInfo(item)
                }
                isCrmStatusNotBind(item) -> {//解绑

                }
                else -> {//失败跳认证页面
                    item.reason = "${it.authReason.text}"
                    skipUniSubmitAuth(item)
                }
            }
        }
    }
}


/**
 * 跳转认证页面
 */
fun skipUniSubmitAuth(item: CarItemBean) {
    when {
        isCrmFail(item) -> {
            RouterManger.param(RouterManger.KEY_TO_OBJ, item)
                .startARouter(ARouterMyPath.UniCarAuthUI)
        }
        else -> {
            RouterManger.param(RouterManger.KEY_TO_OBJ, item)
                .startARouter(ARouterMyPath.CarAuthIngUI)
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
    RouterManger.param(RouterManger.KEY_TO_OBJ, item)
        .startARouter(ARouterMyPath.MineLoveCarInfoUI)
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
//                if (item.status == 4) "失败原因：${item.crmAuthRemake}" else "失败原因：${item.pretrialRemake}"
            }
        }
        3 -> {//CRM认证成功
            "${item.plateNum}------".logE()
            holder.btnAddCarNum.visibility = View.VISIBLE
            if (item.plateNum.isNullOrEmpty() || "无牌照" == item.plateNum) {
                holder.btnAddCarNum.apply {
                    text = "添加车牌"
                    setBackgroundResource(R.drawable.shape_car_auth_btn_bg)
                    setTextColor(Color.parseColor("#ffffff"))
                    setOnClickListener {
                        RouterManger.param("value", item.vin)
                            .startARouter(ARouterMyPath.AddCardNumTransparentUI)
                    }
                }
            } else {
                holder.btnAddCarNum.apply {
                    setBackgroundResource(R.drawable.shape_car_num_btn_bg)
                    setTextColor(Color.parseColor("#00095B"))
                    text = "${item.plateNum}"
                }
            }
        }
    }
}

/**
 * 列表incall提示
 */
private fun inCallHint(
    hintStats: Int,
    holder: ItemCarAuthBinding,
    item: CarItemBean,
    isSetDefCar: Boolean
) {
//    holder.hintLayout.visibility = View.GONE
//    holder.hintReason.visibility = View.GONE
//    holder.hintLine.visibility = View.GONE
//    holder.hintTitle.visibility = View.GONE
//    when (hintStats) {
//        1, 2 -> {//审核中和审核失败
//            holder.carNumCard.visibility = View.INVISIBLE
//            holder.openCall.visibility = View.INVISIBLE
//            holder.hintLayout.visibility = View.VISIBLE
//            holder.hintReason.visibility = View.VISIBLE
//            holder.hintReason.text = "审核中，审核时间1-3个工作日"
//            if (hintStats == 2) {//认证失败
//                holder.hintReason.text = "失败原因：${item.incallAuthRemake}"
//            }
//        }
//        3 -> {
//            holder.openCall.visibility = View.VISIBLE
//            holder.carNumCard.visibility = View.VISIBLE
//            //原来列表的基础增加这两个字段（是否开通车控通过realnameAuthStatus字段判断;是否CRM认证 通过status==null
//            //（表示并未提交过crm认证）反之则按照以前的状态值判断）
//            if (item.plateNum.isNullOrEmpty()) {
//                holder.carNumCard.text = "添加车牌"
//                holder.carNumCard.setTextColor(Color.parseColor("#FC883B"))
//            } else {
//                holder.carNumCard.text = "${item.plateNum}"
//                holder.carNumCard.setTextColor(Color.parseColor("#182634"))
//            }
//            if (!isSetDefCar && (isCrmFail(item) || item.status == 0)) {//incall成功，crm失败,未认证
//                holder.hintLayout.visibility = View.VISIBLE
//                holder.hintReason.visibility = View.VISIBLE
//                holder.hintReason.text = "车主认证资料过期，避免车主权益受限，请更新认证资料"
//                holder.hintLine.visibility = View.VISIBLE
//                holder.hintTitle.visibility = View.VISIBLE
//                holder.hintTitle.text = "更新资料"
//                //incall认证成功，crm认证需要更新
//                holder.hintTitle.setOnClickListener {
//                    item.reason =
//                        if (item.status == 4) item.crmAuthRemake else item.pretrialRemake
//                    skipUniSubmitAuth(
//                        holder.hintLayout.context,
//                        item
//                    )
//                }
//            }
//        }
//    }
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