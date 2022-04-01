package com.changanford.shop.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun ChooseCouponsCompose(dataList:MutableList<CouponsItemBean>?=null) {
    val selectedTag = remember { mutableStateOf("") }
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "当前可选1张", color = colorResource(R.color.color_33), fontSize = 13.sp, modifier = Modifier.padding(start = 20.dp))
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)){
            if(dataList!=null&&dataList.size>0){
                items(dataList){item->
                    ItemCouponsCompose(item,selectedTag)
                }
            }
        }
        Button(onClick = {
            LiveDataBus.get().with(LiveDataBusKey.COUPONS_CHOOSE_BACK, CouponsItemBean::class.java)
                .postValue(null)
        },shape = RoundedCornerShape(20.dp), contentPadding = PaddingValues(horizontal = 0.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.color_00095B)),
            modifier = Modifier.height(40.dp)) {
            Text(stringResource(R.string.str_determine),fontSize = 15.sp,color = Color.White)
        }
    }
}
@Composable
private fun ItemCouponsCompose(itemBean: CouponsItemBean?,selectedTag:MutableState<String>){
    itemBean?.apply {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(color = Color.White, shape = RoundedCornerShape(5.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                selectedTag.value = couponId ?: "0"
            }, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier
                .width(110.dp)
                .fillMaxHeight()
                .background(color = colorResource(R.color.color_8195C8), shape = RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp)), horizontalAlignment = Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)) {
                        append(stringResource(id = R.string.str_rmb))
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)) {
                        append(conditionMoney)
                    }
                    withStyle(style = SpanStyle(color = colorResource(R.color.color_f6), fontSize = 11.sp)) {
                        append("\n${desc?:""}")
                    }
                })
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.SpaceAround) {
                Text(text = couponName?:"", fontSize = 14.sp,color= colorResource(R.color.color_33))
                Text(text = "${simpleDateFormat.format(validityBeginTime)}-${simpleDateFormat.format(validityEndTime)}", fontSize = 11.sp,color= colorResource(R.color.color_99))
            }
            Image(painter = painterResource(if(selectedTag.value==couponId) R.mipmap.shop_order_cb_1 else R.mipmap.shop_order_cb_0), contentDescription =null )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}
