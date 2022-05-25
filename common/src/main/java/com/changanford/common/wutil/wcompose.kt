package com.changanford.common.wutil

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import com.changanford.common.R
import com.changanford.common.utilext.GlideUtils

/**
 * @Author : wenke
 * @Time : 2022/5/25
 * @Description : compose
 */
@Composable
fun WImage(imgUrl:String?=null,
           modifier: Modifier = Modifier,
           contentScale: ContentScale = ContentScale.Crop,
){
    Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(imgUrl) ?: R.mipmap.head_default,
        builder = {placeholder(R.mipmap.head_default)}),
        contentScale = contentScale,
        contentDescription =null,modifier = modifier)
}