package com.changanford.my.compose

import android.text.TextUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.changanford.common.compose.*
import com.changanford.common.util.MConstant
import com.changanford.my.viewmodel.CarAuthViewModel

/**
 *  文件名：DeleteCarScreen
 *  创建者: zcy
 *  创建日期：2022/1/13 13:57
 *  描述: TODO
 *  修改描述：TODO
 */
@Composable
fun DeleteCarScreen(
    carAuthViewModel: CarAuthViewModel,
    deleteTips:String?="",
    serversTips:String?="",
    onGetSmsClick: (mobile: String) -> Unit,
    onSubmitClick: (mobile: String, sms: String) -> Unit
) {
    var mobileInput by remember { mutableStateOf("${MConstant.mine_phone}") }
    var smsInput by remember { mutableStateOf("") }
    var btn by remember { mutableStateOf(true) }
    var mobileHint by remember { mutableStateOf("请输入手机号") }
    var smsHint by remember { mutableStateOf("请输入短信验证码") }
    val deleteTipStr = if(TextUtils.isEmpty(deleteTips))"车辆删除后，将无法使用被删除车辆的各种车主权益 请谨慎操作。" else deleteTips

    val serversTipStr = if(TextUtils.isEmpty(serversTips))"客服电话：023-989898" else serversTips
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Text(
            text = deleteTipStr!!,
            color = Color_95B,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 52.dp)
                .background(color = Color_2095B)
                .padding(horizontal = 27.dp, vertical = 6.dp)
        )
        Text(
            text = "手机号", fontSize = 13.sp, color = Color_333,
            modifier = Modifier.padding(top = 18.dp, start = 19.dp)
        )
        OutlinedTextField(
            value = mobileInput,
            onValueChange = {
                mobileHint = if (it.length > 11) {
                    "输入手机号不正确"
                } else {
                    "请输入手机号"
                }
                mobileInput = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 13.dp, end = 20.dp)
                .defaultMinSize(minHeight = 44.dp),
            textStyle = TextStyle(color = Color_333, fontSize = 13.sp),
            shape = RoundedCornerShape(2.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color_f2f2, focusedBorderColor = Color_999
            ),
            singleLine = true,
            label = {
                Text(text = mobileHint, color = Color_999, fontSize = 13.sp)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Text(
            text = "手机验证码", fontSize = 13.sp, color = Color_333,
            modifier = Modifier.padding(top = 17.dp, start = 19.dp)
        )
        OutlinedTextField(
            value = smsInput,
            onValueChange = {
                smsInput = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 13.dp, end = 20.dp)
                .defaultMinSize(minHeight = 44.dp),
            textStyle = TextStyle(color = Color_333, fontSize = 13.sp),
            shape = RoundedCornerShape(2.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color_f2f2, focusedBorderColor = Color_999
            ),
            singleLine = true,
            label = {
                Text(text = smsHint, color = Color_999, fontSize = 13.sp)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                Text(
                    text = carAuthViewModel.smsGetHint,
                    color = Color_95B,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .clickable(
                            onClick = {
                                onGetSmsClick(mobileInput)
                            },
                            enabled = carAuthViewModel.btnGetSmsIsEnabled
                        )
                )
            },
        )
        Text(
            text = "无法收到短信请联系客服处理。", color = Color_666, fontSize = 13.sp,
            modifier = Modifier.padding(top = 20.dp, start = 20.dp)
        )

        if (serversTipStr != null) {
            Text(
                text =serversTipStr, color = Color_333, fontSize = 13.sp,
                modifier = Modifier.padding(top = 13.dp, start = 20.dp)
            )
        }

        Button(
            onClick = { onSubmitClick(mobileInput, smsInput) },
            modifier = Modifier
                .padding(start = 20.dp, top = 50.dp, end = 20.dp)
                .height(40.dp)
                .fillMaxWidth()
                .align(Alignment.End),
            shape = RoundedCornerShape(percent = 50),
            elevation = ButtonDefaults.elevation(4.dp, 8.dp, 2.dp),    //按钮正视图高度，类似阴影这种效果，体现层次感
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color_95B,
                disabledBackgroundColor = Color_20b89
            ), enabled = btn
        ) {
            Text(text = "提交", color = Color.White, fontSize = 15.sp)
        }
    }
}

