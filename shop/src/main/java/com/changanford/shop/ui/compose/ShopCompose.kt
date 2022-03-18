package com.changanford.shop.ui.compose

import android.text.TextUtils
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.changanford.common.R
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils

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
/**
 * 商品推荐列表
* */
@Composable
fun RecommendListCompose(dataBean:MutableList<GoodsItemBean>?){
    if(dataBean==null)return
    LazyColumn{
       items(dataBean.size){position ->
           RecommendItemCompose(position,dataBean[position])
       }
    }
}
/**
 * 推荐列表item
* */
@Composable
private fun RecommendItemCompose(position:Int,itemData:GoodsItemBean?){
    itemData?.apply {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(60.dp).padding(top = 10.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(15.dp))
            //排名
            Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center){
                if(position<3){
                    Image(painter = painterResource(
                        when (position) {
                            0 -> R.mipmap.ic_ranking_1
                            1 -> R.mipmap.ic_ranking_2
                            else -> R.mipmap.ic_ranking_3
                        }
                    ), contentDescription = null)
                }else{
                    Text(text = "$position",color= colorResource(R.color.color_D1D2D7), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(11.dp))
            //封面
            Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(getImgPath()) ?: R.mipmap.head_default,
                builder = {placeholder(R.mipmap.head_default)}),
                contentScale = ContentScale.Crop,
                contentDescription =null,modifier = Modifier.size(60.dp).clip(RoundedCornerShape(5.dp)))
            Spacer(modifier = Modifier.width(11.dp))
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween){
                //标题
                Text(text = spuName,color= colorResource(R.color.color_33), fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                //福币价格,销量
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text ="$priceFb${stringResource(com.changanford.shop.R.string.str_integral)}",color= colorResource(R.color.color_33), fontSize = 14.sp)
                    Text(text =stringResource(com.changanford.shop.R.string.str_hasChangeXa,"$salesCount"),color= colorResource(R.color.color_99), fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}