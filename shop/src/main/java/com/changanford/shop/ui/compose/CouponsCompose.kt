package com.changanford.shop.ui.compose

import android.app.Activity
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
fun ChooseCouponsCompose(act:Activity,defaultItemBean:CouponsItemBean?=null,dataList:MutableList<CouponsItemBean>?=null) {
    val findItem=dataList?.find { it.isAvailable }
    val selectedTag = remember { mutableStateOf(defaultItemBean?:dataList?.get(0)) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(20.dp)) {
        Text(text = stringResource(if(findItem!=null) R.string.str_currentlySelect1Card else R.string.str_noCouponsAvailableMoment), color = colorResource(R.color.color_33), fontSize = 13.sp)
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp),modifier = Modifier
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
                .postValue(selectedTag.value)
            act.finish()
        },shape = RoundedCornerShape(20.dp), contentPadding = PaddingValues(horizontal = 0.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.color_00095B)),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)) {
            Text(stringResource(R.string.str_determine),fontSize = 15.sp,color = Color.White)
        }
    }
}
@Composable
private fun ItemCouponsCompose(itemBean: CouponsItemBean?,selectedTag:MutableState<CouponsItemBean?>){
    itemBean?.apply {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(color = Color.White, shape = RoundedCornerShape(5.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                if (isAvailable) selectedTag.value = itemBean
            }, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier
                .width(110.dp)
                .fillMaxHeight()
                .background(
                    color = colorResource(if (isAvailable) R.color.color_8195C8 else R.color.color_C6CEE4),
                    shape = RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp)
                ), horizontalAlignment = Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                Text(buildAnnotatedString {
                    //非折扣券
                    if(discountType!="DISCOUNT"){
                        withStyle(style = SpanStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.str_rmb))
                        }
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)) {
                        append(if(discountType=="DISCOUNT") couponRatio?:"" else couponMoney?:"")
                    }
                    //折扣券
                    if(discountType=="DISCOUNT"){
                        withStyle(style = SpanStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.str_fold))
                        }
                    }
//                    withStyle(style = SpanStyle(color = colorResource(R.color.color_f6), fontSize = 11.sp)) {
//                        append("\n${desc?:""}")
//                    }
                })
                Text(text =when(discountType){
                    //立减-无门槛
                    "LEGISLATIVE_REDUCTION"-> stringResource(id = R.string.str_noThreshold)
                    else ->"满${conditionMoney}元可用"
                },color = colorResource(R.color.color_f6), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 16.dp))

            }
            Spacer(modifier = Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = couponName?:"", fontSize = 14.sp,color= colorResource(R.color.color_33), modifier = Modifier.weight(1f))
                Text(text = "${simpleDateFormat.format(validityBeginTime)}-${simpleDateFormat.format(validityEndTime)}", fontSize = 11.sp,color= colorResource(R.color.color_99))
                Spacer(modifier = Modifier.height(15.dp))
            }
            Image(painter = painterResource(if(!isAvailable) R.mipmap.ic_cb_disable else if(selectedTag.value==itemBean) R.mipmap.shop_order_cb_1 else R.mipmap.shop_order_cb_0), contentDescription =null )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}
