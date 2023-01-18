package com.changanford.my.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.DaySignBean
import com.changanford.common.bean.Sign7DayBean
import com.changanford.common.constant.HawkKey
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.testCalendar
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.my.R
import com.github.gzuliyujiang.wheelpicker.TimePicker
import com.orhanobut.hawk.Hawk


@Composable
fun dailySignCompose(daySignBean: DaySignBean? = null) {

    Column(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(18.dp)
            .background(Color.White)
            .padding(18.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "每日签到", fontSize = 16.sp, color = Color(0xff666666))
                Text(
                    text = "签到规则 >",
                    fontSize = 12.sp,
                    color = Color(0xff999999),
                    modifier = Modifier
                        .clickable {
                            JumpUtils.instans?.jump(1, MConstant.H5_SIGN_PRESENT_AGREEMENT)
                        }
                        .padding(start = 12.dp)
                        .weight(1f)
                )
                Text(
                    text = "签到提醒",
                    fontSize = 11.sp,
                    color = Color(0xff999999),
                    modifier = Modifier
                        .padding(end = 9.dp)
                )
            }

            var isOpenTips by remember {
                mutableStateOf(Hawk.get(HawkKey.IS_OPEN_SIGN_IN_TIPS, false)&&(System.currentTimeMillis()-Hawk.get(HawkKey.OPEN_SIGN_IN_TIPS_TIME, 0) > 30*24*60*60*1000))
            }
            val tipsImage = if (isOpenTips) {
                R.mipmap.sign_in_tips_open
            } else R.mipmap.sign_in_tips_close

            Image(
                painter = painterResource(id = tipsImage),
                contentDescription = "",
                modifier = Modifier
                    .height(21.dp)
                    .width(38.dp)
                    .clickable {
                        signInTipsClick(isOpenTips) {
                            isOpenTips = it
                        }
                    }
            )
        }
        var canSign = daySignBean == null || MConstant.token.isNullOrEmpty()
        var hasGift = false
        daySignBean?.sevenDays?.forEach {
            if (it.signStatus == 2) {
                canSign = true
            }
            if (it.luckyBlessingBagId != 0 == true) {
                hasGift = true
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(1f)
        ) {
            if (daySignBean?.sevenDays?.isNotEmpty() == true) {
                daySignBean.sevenDays?.forEach {
                    signOneDay(it, hasGift)
                }
            } else {
                repeat(7) {
                    signOneDay(hasGift = hasGift)
                }
            }
        }
        Text(
            text = "连续签到赢大礼 ${if (daySignBean != null && !MConstant.token.isNullOrEmpty()) "，已连续签到${daySignBean?.ontinuous ?: 0}天" else ""}",
            fontSize = 10.sp,
            color = Color(0xff999999),
            modifier = Modifier.padding(top = 10.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(top = 10.dp)
                .height(45.dp)
                .clip(shape = RoundedCornerShape(5.dp))
                .background(Color(if (canSign) 0xffE5EFFF else 0xffDDDDDD))
                .clickable {
                    if (canSign) {
                        JumpUtils.instans?.jump(37)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (canSign) "立即签到" else "已签到",
                fontSize = 15.sp,
                color = Color(if (canSign) 0xff5F9DFC else 0xffF8FAFA)
            )
        }

    }


}


private fun signInTipsClick(isOpenTips: Boolean,result: (Boolean) -> Unit) {
    if (MConstant.token.isEmpty()) {
        startARouter(ARouterMyPath.SignUI)
        return
    }
    if (isOpenTips){
        testCalendar(2){
            Hawk.put(HawkKey.OPEN_SIGN_IN_TIPS_TIME, 0)
            Hawk.put(HawkKey.IS_OPEN_SIGN_IN_TIPS, false)
            result(false)
            "删除成功".toast()
        }
    }else {
        showTimePicker { it1, it2 ->
            //添加事件
            var time = TimeUtils.MillisTo_M_H(System.currentTimeMillis())//yyyy.MM.dd HH:mm
            testCalendar(1,TimeUtils.MillisTo_M_H_REVERSE(time.substring(0,11).plus("$it1:$it2"))){
                Hawk.put(HawkKey.OPEN_SIGN_IN_TIPS_TIME, System.currentTimeMillis())
                Hawk.put(HawkKey.IS_OPEN_SIGN_IN_TIPS, true)
                result(true)
                "添加成功".toast()
            }
        }
    }
}


@Preview("single")
@Composable
fun signOneDay(bean: Sign7DayBean? = null, hasGift: Boolean = true) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(color = Color.White)
    ) {
        if (hasGift) {
            if (bean?.luckyBlessingBagId != 0 == true) {
                Image(
                    painter = painterResource(R.mipmap.icon_sign_gift),
                    contentDescription = "",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            if ((bean?.luckyBlessingBagId ?: 0) != 0) {
                                JumpUtils.instans?.jump(
                                    1, "${MConstant.H5_SIGN_PRESENT}${bean?.luckyBlessingBagId}"
                                )
                            }
                        }
                )
            } else {
                Box(modifier = Modifier.size(25.dp))
            }
        }
        Box(
            modifier = Modifier
                .clip(
                    shape =
                    RoundedCornerShape(2.dp)
                )
                .run {
                    when (bean?.signStatus ?: 0) {
                        1, 2 -> background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xffFFF4E2),
                                    Color(0xffFFE9C8),
                                )
                            )
                        ).padding(top = 10.dp, start = 7.dp, end = 7.dp, bottom = 5.dp)
                        3 -> background(color = Color(0xffF8FAFB)).padding(
                            top = 10.dp,
                            start = 7.dp,
                            end = 7.dp,
                            bottom = 5.dp
                        )
                        else -> background(color = Color(0xffF8FAFB)).padding(
                            top = 10.dp,
                            start = 7.dp,
                            end = 7.dp,
                            bottom = 5.dp
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "+${bean?.fb ?: 1}", fontSize = 10.sp, color = Color(0xff999999))
                ConstraintLayout(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .width(24.dp)
                        .height(24.dp)
                ) {
                    val (coin, check) = createRefs()
                    Image(
                        painter = painterResource(id = R.mipmap.icon_sign_coin),
                        contentDescription = "",
                        modifier = Modifier
                            .size(18.dp)
                            .constrainAs(coin) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )
                    if ((bean?.signStatus ?: 3) == 1) {
                        Image(
                            painter = painterResource(id = R.mipmap.icon_sign_signed),
                            contentDescription = "",
                            modifier = Modifier
                                .size(10.dp)
                                .constrainAs(check) {
                                    start.linkTo(coin.end)
                                    end.linkTo(coin.end)
                                    top.linkTo(coin.bottom)
                                    bottom.linkTo(coin.bottom)
                                }
                        )
                    }
                }
            }
        }
        Text(
            text = "${bean?.days ?: 1}天",
            fontSize = 10.sp,
            color = Color(if (bean?.signStatus == 1) 0xff333333 else 0xff999999)
        )

    }
}

fun showTimePicker(result:(String,String)->Unit){
    var timePicker = TimePicker(BaseApplication.curActivity)
    timePicker.apply {
        setTitle("每日提醒时间")
        setOnTimePickedListener { hour, minute, second ->
            result("$hour","$minute")
        }
        show()
    }
}