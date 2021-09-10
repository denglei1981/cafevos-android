package com.changanford.my.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.R

/**
 *  文件名：SettingUI
 *  创建者: zcy
 *  创建日期：2021/9/9 13:40
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineSettingUI)
class SettingUI : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessageCard(Message("author", "body"))
        }
    }

    @Composable
    fun MessageCard(message: Message) {
        Row {
            Image(
                painter = painterResource(id = R.mipmap.shareicon),
                contentDescription = "我是一个头像",
                modifier = Modifier.size(50.dp).clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.primary),
            )
            Column {
                Text(message.author)
                Text(message.body)
            }
        }
    }

    @Preview
    @Composable
    fun PreviewMessageCard() {
        MessageCard(Message("author", "body"))
    }

    data class Message(var author: String, var body: String)
}