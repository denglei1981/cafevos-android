package com.changanford.my.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.changanford.my.R
import com.github.gzuliyujiang.wheelpicker.TimePicker
import com.orhanobut.hawk.Hawk


@Composable
fun DailySignCompose(daySignBean: DaySignBean? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val annotatedString = buildAnnotatedString {
                    append("已连续签到")
                    withStyle(style = SpanStyle(color = Color(0xff1700f4))) {
                        append("${daySignBean?.ontinuous ?: 0}")
                    }
                    append("天")
                }
                Text(
                    text = annotatedString,
                    fontSize = 16.sp,
                    color = Color(0xff161616),
                )
//                Text(text = "每日签到", fontSize = 16.sp, color = Color(0xff666666))

                Text(
                    text = "签到提醒",
                    fontSize = 13.sp,
                    color = Color(0x80161616),
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
            }

            var isOpenTips by remember {
                mutableStateOf(
                    Hawk.get(
                        HawkKey.IS_OPEN_SIGN_IN_TIPS,
                        false
                    ) && (System.currentTimeMillis() - Hawk.get(
                        HawkKey.OPEN_SIGN_IN_TIPS_TIME,
                        0
                    ) > 30 * 24 * 60 * 60 * 1000)
                )
            }
            val tipsImage = if (isOpenTips) {
                R.mipmap.sign_in_tips_open
            } else R.mipmap.sign_in_tips_close

            Image(
                painter = painterResource(id = tipsImage),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 4.dp)
                    .height(21.dp)
                    .width(37.dp)
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
//        if (daySignBean != null && !MConstant.token.isNullOrEmpty()) {
//            val mSignDay = daySignBean.ontinuous ?: 0
//            if (mSignDay > 0) {
//
//            }
//        }
        Text(
            text = "签到规则 > ",
            fontSize = 12.sp,
            color = Color(0xff999999),
            textAlign = TextAlign.Right,
            modifier = Modifier
                .clickable {
                    JumpUtils.instans?.jump(1, MConstant.H5_SIGN_PRESENT_AGREEMENT)
                }
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(top = 10.dp)
                .height(45.dp)
                .clip(shape = RoundedCornerShape(23.dp))
                .background(Color(if (canSign) 0x081700f4 else 0x80a6a6a6))
//                .background(Color(0xfff8f7ff))
                .clickable {
                    if (canSign) {
                        JumpUtils.instans?.jump(37)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (canSign) "立即签到" else "已签到",
                fontSize = 16.sp,
                color = Color(if (canSign) 0xff1700f4 else 0x4d161616)
//                color = Color(0xff1700f4)
            )
        }

    }


}


private fun signInTipsClick(isOpenTips: Boolean, result: (Boolean) -> Unit) {
    if (MConstant.token.isEmpty()) {
        startARouter(ARouterMyPath.SignUI)
        return
    }
    if (isOpenTips) {
        testCalendar(2) {
            Hawk.put(HawkKey.OPEN_SIGN_IN_TIPS_TIME, 0)
            Hawk.put(HawkKey.IS_OPEN_SIGN_IN_TIPS, false)
            result(false)
            "签到提醒关闭成功".toast()
        }
    } else {
        showTimePicker { it1, it2 ->
            //添加事件
            var time = TimeUtils.MillisTo_M_H(System.currentTimeMillis())//yyyy.MM.dd HH:mm
            testCalendar(
                1,
                TimeUtils.MillisTo_M_H_REVERSE(time.substring(0, 11).plus("$it1:$it2"))
            ) {
                Hawk.put(HawkKey.OPEN_SIGN_IN_TIPS_TIME, System.currentTimeMillis())
                Hawk.put(HawkKey.IS_OPEN_SIGN_IN_TIPS, true)
                result(true)
                "签到提醒开启成功".toast()
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
                        .padding(bottom = 2.dp)
                        .size(32.dp, 26.dp)
                        .clickable {
                            if ((bean?.luckyBlessingBagId ?: 0) != 0) {
                                JumpUtils.instans?.jump(
                                    1, "${MConstant.H5_SIGN_PRESENT}${bean?.luckyBlessingBagId}"
                                )
                            }
                        }
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(bottom = 2.dp)
                        .size(32.dp, 26.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(
                    shape =
                    RoundedCornerShape(4.dp)
                )
                .run {
                    when (bean?.signStatus ?: 0) {
                        1, 2 -> background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x261700f4),
                                    Color(0x261700f4),
                                )
                            )
                        ).padding(top = 4.dp, start = 9.dp, end = 9.dp, bottom = 5.dp)

                        3 -> background(color = Color(0x0d000000)).padding(
                            top = 4.dp,
                            start = 9.dp,
                            end = 9.dp,
                            bottom = 5.dp
                        )

                        else -> background(color = Color(0x0d000000)).padding(
                            top = 4.dp,
                            start = 9.dp,
                            end = 9.dp,
                            bottom = 5.dp
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ConstraintLayout(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .width(24.dp)
                        .height(24.dp)
                ) {
                    val (coin, check) = createRefs()
                    val useRes = if ((bean?.signStatus
                            ?: 3) == 1
                    ) R.mipmap.icon_has_sign_top else R.mipmap.icon_sign_coin
                    Image(
                        painter = painterResource(id = useRes),
                        contentDescription = "",
                        modifier = Modifier
                            .size(24.dp)
                            .constrainAs(coin) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )
//                    if ((bean?.signStatus ?: 3) == 1) {
//                        Image(
//                            painter = painterResource(id = R.mipmap.icon_sign_signed),
//                            contentDescription = "",
//                            modifier = Modifier
//                                .size(10.dp)
//                                .constrainAs(check) {
//                                    start.linkTo(coin.end)
//                                    end.linkTo(coin.end)
//                                    top.linkTo(coin.bottom)
//                                    bottom.linkTo(coin.bottom)
//                                }
//                        )
//                    }
                }
                Text(
                    text = "+${bean?.fb ?: 1}",
                    fontSize = 12.sp,
                    color = Color(0x80161616),
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = "${bean?.days ?: 1}天",
            fontSize = 12.sp,
            color = Color(if (bean?.signStatus == 1) 0x80161616 else 0xff161616)
        )

    }
}

fun showTimePicker(result: (String, String) -> Unit) {
    var timePicker = TimePicker(BaseApplication.curActivity)
    timePicker.apply {
        setTitle("每日提醒时间")
        setOnTimePickedListener { hour, minute, second ->
            result("$hour", "$minute")
        }
        wheelLayout.setIndicatorColor(ContextCompat.getColor(context, R.color.transparent))
        wheelLayout.setSelectedTextColor(
            ContextCompat.getColor(
                topLineView.context,
                R.color.color_1700F4
            )
        )
        titleView.post {
            titleView.text = "每日提醒时间"
            titleView.textSize = 16f
//            titleView.setTextColor(
//                ContextCompat.getColor(
//                    topLineView.context,
//                    R.color.color_1700F4
//                )
//            )
        }
        topLineView.post {
            headerView.setBackgroundColor(
                ContextCompat.getColor(
                    topLineView.context,
                    R.color.white
                )
            )
            topLineView.isVisible = false
//            okView.setTextColor(ContextCompat.getColor(topLineView.context, R.color.color_1700F4))
//            cancelView.setTextColor(
//                ContextCompat.getColor(
//                    topLineView.context,
//                    R.color.color_1700F4
//                )
//            )
        }
        show()
    }

}