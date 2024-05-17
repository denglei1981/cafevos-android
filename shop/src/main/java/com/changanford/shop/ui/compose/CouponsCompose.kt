package com.changanford.shop.ui.compose

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.shop.R
import java.text.SimpleDateFormat

/**
 * @Author : wenke
 * @Time : 2022/4/1
 * @Description : 优惠券
 */
private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd")

/**
 * 选择优惠券
 * */
@Composable
fun ChooseCouponsCompose(
    act: Activity,
    defaultItemBean: CouponsItemBean? = null,
    dataList: List<CouponsItemBean>? = null
) {
    val filter = dataList?.filter { it.isAvailable }
//    val default= defaultItemBean ?: if(dataList!=null&& dataList.isNotEmpty())dataList[0] else null
    val selectedTag = remember { mutableStateOf(defaultItemBean) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
//            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp),
            text = if (filter != null && filter.isNotEmpty()) "当前有${filter.size}张可选" else stringResource(
                R.string.str_noCouponsAvailableMoment
            ), color = colorResource(R.color.color_8016), fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(17.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (dataList != null && dataList.isNotEmpty()) {
                items(dataList) { item ->
                    ItemCouponsCompose(item, selectedTag)
                }
            }
        }
        Column(
            modifier = Modifier
                .background(
                    color = colorResource(
                        R.color.white
                    )
                )
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
//                onClick = {
//                    LiveDataBus.get()
//                        .with(LiveDataBusKey.COUPONS_CHOOSE_BACK, CouponsItemBean::class.java)
//                        .postValue(selectedTag.value)
//                    act.finish()
//                },
//                shape = RoundedCornerShape(20.dp),
//                contentPadding = PaddingValues(horizontal = 0.dp),
//                colors = ButtonDefaults.buttonColors(
//                    backgroundColor = if (selectedTag.value == null) colorResource(
//                        R.color.color_80a6
//                    ) else colorResource(R.color.color_1700F4)
//                ),
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = if (selectedTag.value == null) colorResource(
                            R.color.color_80a6
                        ) else colorResource(R.color.color_1700F4)
                    )
                    .clickable {
                        LiveDataBus
                            .get()
                            .with(LiveDataBusKey.COUPONS_CHOOSE_BACK, CouponsItemBean::class.java)
                            .postValue(selectedTag.value)
                        act.finish()
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.str_determine),
                    fontSize = 15.sp,
                    color = if (selectedTag.value == null) colorResource(R.color.color_4d16) else colorResource(
                        R.color.white
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

    }
}

@Composable
private fun ItemCouponsCompose(
    itemBean: CouponsItemBean?,
    selectedTag: MutableState<CouponsItemBean?>
) {
    itemBean?.apply {
        Row(modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .height(110.dp)
            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                if (isAvailable) {
                    val tag = selectedTag.value
                    selectedTag.value = if (tag != itemBean) itemBean else null
                }
            }, verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(94.dp)
                    .fillMaxHeight()
                    .background(
                        color = colorResource(if (isAvailable) R.color.color_261700f4 else R.color.color_141700f4),
                        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(buildAnnotatedString {
                    //非折扣券
                    if (discountType != "DISCOUNT") {
                        withStyle(
                            style = SpanStyle(
                                color = colorResource(if (isAvailable) R.color.color_1700f4 else R.color.color_4d1700f4),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(stringResource(id = R.string.str_rmb))
                        }
                    }
                    withStyle(
                        style = SpanStyle(
                            color = colorResource(if (isAvailable) R.color.color_1700f4 else R.color.color_4d1700f4),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(
                            if (discountType == "DISCOUNT") couponRatio ?: "" else "$couponMoney"
                        )
                    }
                    //折扣券
                    if (discountType == "DISCOUNT") {
                        withStyle(
                            style = SpanStyle(
                                color = colorResource(if (isAvailable) R.color.color_1700f4 else R.color.color_4d1700f4),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(stringResource(R.string.str_fold))
                        }
                    }
//                    withStyle(style = SpanStyle(color = colorResource(R.color.color_f6), fontSize = 11.sp)) {
//                        append("\n${desc?:""}")
//                    }
                })
                Text(
                    text = when (discountType) {
                        //立减-无门槛
                        "LEGISLATIVE_REDUCTION" -> stringResource(id = R.string.str_noThreshold)
                        else -> "满${conditionMoney}元可用"
                    },
                    color = colorResource(if (isAvailable) R.color.color_1700f4 else R.color.color_4d1700f4),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

            }
            Spacer(modifier = Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = couponName ?: "",
                    fontSize = 14.sp,
                    color = colorResource(R.color.color_16),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${simpleDateFormat.format(validityBeginTime)}-${
                        simpleDateFormat.format(
                            validityEndTime
                        )
                    }", fontSize = 10.sp, color = colorResource(R.color.color_8016)
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
            Image(
                painter = painterResource(if (!isAvailable) R.mipmap.ic_cb_disable else if (selectedTag.value == itemBean) R.mipmap.shop_order_cb_1 else R.mipmap.shop_order_cb_0),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}
