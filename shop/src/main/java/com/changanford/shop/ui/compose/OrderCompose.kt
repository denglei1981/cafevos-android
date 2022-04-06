package com.changanford.shop.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.PayWayBean
import com.changanford.common.utilext.GlideUtils
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
 * 订单item
* */
@Composable
fun OrderGoodsItem(imgWidth:Int=90,itemBean: OrderItemBean? =null){
    if(itemBean?.skuOrderVOList==null)return
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(imgWidth.dp)
        .background(Color.White), contentAlignment = Alignment.CenterEnd){
        Row(modifier = Modifier.fillMaxWidth()) {
            for ((i,item)in itemBean.skuOrderVOList!!.withIndex()){
                item.apply {
                    if(0==i) Spacer(modifier = Modifier.width(20.dp))
                    Box(modifier = Modifier.padding(end = 6.dp))  {
                        Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(skuImg) ?: com.changanford.common.R.mipmap.head_default,
                            builder = {placeholder(com.changanford.common.R.mipmap.head_default)}),
                            contentScale = ContentScale.Crop,
                            contentDescription =null,modifier = Modifier
                                .size(imgWidth.dp)
                                .clip(RoundedCornerShape(5.dp)))
                        Box(modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                            .background(
                                color = colorResource(R.color.color_B300095B),
                                shape = RoundedCornerShape(5.dp)
                            ), contentAlignment = Alignment.Center){
                            Text(text = getTag(), color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
        Column(modifier = Modifier
            .fillMaxHeight()
            .defaultMinSize(minWidth = 67.dp)
            .background(colorResource(id = R.color.color_DBWhite))
            .padding(start = 8.dp, end = 20.dp)
            , horizontalAlignment = Alignment.End,verticalArrangement = Arrangement.Center) {
            Text(text = "￥${itemBean.rmb}", fontSize = 14.sp, color = colorResource(R.color.color_33))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "共${itemBean.totalNum}件", fontSize = 11.sp, color = colorResource(R.color.color_74889D))
        }
    }
}
