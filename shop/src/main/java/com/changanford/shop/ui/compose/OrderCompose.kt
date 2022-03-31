package com.changanford.shop.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.PayWayBean
import com.changanford.shop.R

/**
 * @Author : wenke
 * @Time : 2022/3/14
 * @Description : OrderCompose
 */
/**
 * 支付方式
* */
@Composable
fun PayWayCompose(){
    val payWayArr=ArrayList<PayWayBean>()
    for (i in 0..2){
        payWayArr.add(PayWayBean(id = i, isCheck = remember {mutableStateOf(false)}, rmbPrice = "0", fbPrice = "0"))
    }
    var inputFb = remember { mutableStateOf("") }
    Column{
        Spacer(modifier = Modifier
            .height(10.dp)
            .background(color = colorResource(R.color.color_F4)))
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp, 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.str_payWay),color= colorResource(R.color.color_33), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = stringResource(R.string.str_currencyAvailableFb),color= colorResource(R.color.color_00095B), fontSize = 12.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Image(painter = painterResource(R.mipmap.ic_shop_fb_42), contentDescription = null)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "0）",color= colorResource(R.color.color_00095B), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row {
                //福币+人民币
                Row(verticalAlignment = Alignment.CenterVertically,
                   modifier = Modifier
                       .height(36.dp)
                       .border(
                           width = if (payWayArr[0].isCheck!!.value) 0.dp else 0.5.dp,
                           color = colorResource(R.color.color_ee),
                           shape = RoundedCornerShape(18.dp)
                       )
                       .background(
                           color = if (payWayArr[0].isCheck!!.value) colorResource(R.color.color_F2F4F9) else Color.White,
                           shape = RoundedCornerShape(18.dp)
                       )
                       .padding(horizontal = 16.dp)
                       .clickable(
                           indication = null,
                           interactionSource = remember { MutableInteractionSource() }) {
                           payWayArr[0].isCheck!!.value = true
                           payWayArr[1].isCheck!!.value = false
                           payWayArr[2].isCheck!!.value = false
                       }) {
                    Image(painter = painterResource(R.mipmap.ic_shop_fb_42), contentDescription = null)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "13999+¥130",color= colorResource(R.color.color_33), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(36.dp)
                        .border(
                            width = if (payWayArr[1].isCheck!!.value) 0.dp else 0.5.dp,
                            color = colorResource(R.color.color_ee),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .background(
                            color = if (payWayArr[1].isCheck!!.value) colorResource(R.color.color_F2F4F9) else Color.White,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 16.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            payWayArr[1].isCheck!!.value = true
                            payWayArr[0].isCheck!!.value = false
                            payWayArr[2].isCheck!!.value = false
                        }) {
                    Text(text = "¥130",color= colorResource(R.color.color_00095B), fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(36.dp)
                    .border(
                        width = if (payWayArr[2].isCheck!!.value) 0.dp else 0.5.dp,
                        color = colorResource(R.color.color_ee),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .background(
                        color = if (payWayArr[2].isCheck!!.value) colorResource(R.color.color_F2F4F9) else Color.White,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 16.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        payWayArr[2].isCheck!!.value = true
                        payWayArr[0].isCheck!!.value = false
                        payWayArr[1].isCheck!!.value = false
                    }) {
                if(!payWayArr[2].isCheck!!.value)Text(text = "自定义福币支付",color= colorResource(R.color.color_33), fontSize = 14.sp)
                else{
                    Image(painter = painterResource(R.mipmap.ic_shop_fb_42), contentDescription = null)
                    Spacer(modifier = Modifier.width(3.dp))
                    TextField(value = inputFb.value, onValueChange = {
                        inputFb.value= it
                    }, colors =  TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent), textStyle = TextStyle(color = colorResource(R.color.color_00095B), fontSize = 14.sp), placeholder = {
                        Text(text = stringResource(R.string.str_pleaseEnterUseLuckyCurrency), color = colorResource(R.color.color_99), fontSize = 14.sp)
                    }, modifier = Modifier
                        .background(Color.Transparent)
                        .padding(0.dp))
                }
            }
        }

    }
}

/**
 * 确认支付-银联支付
 * */
@Composable
fun UnionPayCompose(dataBean: OrderItemBean?=null){
    //剩余支付时间
    val countdown= remember {mutableStateOf("00:00:00")}
    val payWayArr=ArrayList<PayWayBean>()
    val nameArr= arrayOf(stringResource(R.string.str_wxPay),stringResource(R.string.str_zfbPay),stringResource(R.string.str_unionPayCloudFlashPayment))
    val iconArr= arrayOf(painterResource(R.mipmap.ic_shop_wx),painterResource(R.mipmap.ic_shop_zfb),painterResource(R.mipmap.ic_shop_ysf))
    for ((i,it) in nameArr.withIndex()){
        payWayArr.add(PayWayBean(id = i, isCheck = remember {mutableStateOf(0==i)}, payWayName = it, icon = iconArr[i]))
    }
    val selectedTag = remember { mutableStateOf(payWayArr[0].payWayName) }
    dataBean?.apply {
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = colorResource(R.color.color_F4))) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(26.dp))
                //支付金额
                Text(text = getRMB(), fontSize = 28.sp, color = colorResource(R.color.color_33))
                Spacer(modifier = Modifier.height(14.dp))
                //剩余支付时间
                Text(text = "${stringResource(R.string.str_remainingTimePayment)}${countdown.value}", fontSize = 13.sp, color = colorResource(R.color.color_33))
                Spacer(modifier = Modifier.height(26.dp))
                Divider(color = colorResource(R.color.color_F5), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(5.dp))
                for ((i,item) in payWayArr.withIndex()){
                    item.apply {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 15.dp).clickable(indication = null,interactionSource = remember { MutableInteractionSource() }) {
                                    selectedTag.value=payWayName
                                }) {
                            Image(painter = icon?:painterResource(R.mipmap.ic_shop_wx), contentDescription =null )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = payWayName?:"", color = colorResource(R.color.color_33), fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Image(painter = painterResource(if(selectedTag.value==payWayName) R.mipmap.shop_order_cb_1 else R.mipmap.shop_order_cb_0), contentDescription =null )
                            RadioButton(selected = selectedTag.value==payWayName, onClick = {
                                selectedTag.value=payWayName
                            })
                        }
                    }

                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}
