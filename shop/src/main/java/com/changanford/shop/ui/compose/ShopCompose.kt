package com.changanford.shop.ui.compose

import android.text.TextUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.changanford.common.R
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant

/**
 * @Author : wenke
 * @Time : 2022/3/14
 * @Description : ShopCompose
 */
/**
 * 首页我的积分
 * [fbNumber]我的福币
* */
@Composable
fun HomeMyIntegralCompose(fbNumber:String?=null){
    //是否登录
    val isLogin=!TextUtils.isEmpty(MConstant.token)
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 20.dp, end = 20.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(R.color.color_F5F5F9),
                shape = RoundedCornerShape(5.dp)
            )
            .padding(top = 16.dp, bottom = 14.dp, start = 18.dp, end = 15.dp)) {
            Image(painter = painterResource(R.mipmap.ic_shop_fb), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.str_myFbX,if(isLogin)":$fbNumber" else ""),color= colorResource(R.color.color_33), fontSize = 14.sp,
            modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if(isLogin){
                    WBuriedUtil.clickShopIntegral()
                    JumpUtils.instans?.jump(16)
                }else JumpUtils.instans?.jump(100)
            },shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(horizontal = 14.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.color_01025C)),
                modifier = Modifier.height(32.dp)) {
                Text(stringResource(if(isLogin)R.string.str_earnMoney else R.string.str_loginToView),fontSize = 12.sp,color = Color.White)
            }
        }
    }
}