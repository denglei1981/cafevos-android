package com.changanford.shop.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Column{
        Spacer(modifier = Modifier
            .height(10.dp)
            .background(color = colorResource(R.color.color_F4)))
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp, 16.dp)) {
            Row {
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(color = colorResource(R.color.color_F4), fontSize = 14.sp)) {
                        append(stringResource(R.string.str_payWay))
                    }
                    withStyle(style = SpanStyle(color = colorResource(R.color.color_00095B), fontSize = 12.sp)) {
                        append(stringResource(R.string.str_currencyAvailableFb))
                    }
                })
                Spacer(modifier = Modifier.width(5.dp))
                Image(painter = painterResource(R.mipmap.ic_shop_fb_42), contentDescription = null)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "）",color= colorResource(R.color.color_00095B), fontSize = 12.sp)
            }

        }

    }
}