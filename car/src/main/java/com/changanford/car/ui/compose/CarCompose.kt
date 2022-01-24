package com.changanford.car.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.changanford.car.R
import com.changanford.common.bean.NewCarInfoBean
import com.changanford.common.bean.NewCarTagBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.wutil.WCommonUtil

/**
 * @Author : wenke
 * @Time : 2022/1/20 0020
 * @Description : CarCompose
 */

/**
 * 售后服务
 * */
@Composable
fun AfterSalesService(carInfoBean: NewCarInfoBean?){
    if(carInfoBean?.icons == null)return
    val dataList=carInfoBean.icons?: arrayListOf()
    //一排几列
    val columnSize=3
    //总共几排
    val rowTotal= WCommonUtil.getHeatNumUP("${dataList.size/columnSize.toFloat()}",0).toInt()
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 20.dp, end = 20.dp)) {
        Text(text = carInfoBean.modelName,color = colorResource(R.color.color_33),fontSize = 17.sp)
        Spacer(modifier = Modifier.height(18.dp))
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(5.dp))) {
            Spacer(modifier = Modifier.height(25.dp))
            for (row in 0 until rowTotal){
                val startIndex=row*columnSize
                val endIndex=if(row!=rowTotal-1)(row+1)*columnSize else dataList.size
                val itemList=dataList.slice(startIndex until endIndex)
                val itemListSize=itemList.size
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (i in 0 until columnSize){
                        Box(modifier = Modifier.weight(1f).padding(start = 10.dp, end = 10.dp)) {
                            ItemService(if(itemListSize>i)itemList[i] else null)
                        }
                        if(i<2) Spacer(modifier = Modifier.width(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
/**
 * 售后服务item
 * */
@Composable
private fun ItemService(itemData: NewCarTagBean?){
    itemData?.apply {
        Column(horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier.fillMaxWidth().clickable {
            JumpUtils.instans?.jump(jumpDataType,jumpDataValue)
        }) {
           Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(iconImg) ?: R.mipmap.head_default,
               builder = {placeholder(R.mipmap.head_default)}),
               contentDescription =null,modifier = Modifier.size(32.dp))
           Spacer(modifier = Modifier.height(16.dp))
           Text(text = iconName?:"",fontSize = 12.sp,color = colorResource(R.color.color_33),overflow = TextOverflow.Ellipsis,maxLines = 1)
        }

    }
}
/**
 * 寻找经销商
* */
@Composable
fun LookingDealers(dataBean: NewCarInfoBean?=null){
    dataBean?.apply {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)) {
            Spacer(modifier = Modifier.height(40.dp))
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically) {
                Text(text = modelName,color = colorResource(R.color.color_33),fontSize = 17.sp)
                Image(painter = painterResource(R.mipmap.right_black), contentDescription = null,modifier = Modifier.clickable {

                })
            }
            Spacer(modifier = Modifier.height(17.dp))
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(5.dp))) {
                Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.TopEnd){
                    Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(pic) ?: R.mipmap.head_default,
                        builder = {placeholder(R.mipmap.head_default)}),
                        contentScale = ContentScale.Crop,
                        contentDescription =null,modifier = Modifier
                            .fillMaxWidth()
                            .height(154.dp)
                            .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)))
                    Row(modifier = Modifier.padding(top = 14.dp,end = 12.dp)) {
                        Box(contentAlignment = Alignment.Center,modifier = Modifier
                            .defaultMinSize(65.dp, 24.dp)
                            .background(
                                colorResource(R.color.color_9900142E),
                                shape = RoundedCornerShape(2.dp)
                            )) {
                            Text(text = stringResource(R.string.str_fromYouRecently),color = Color.White,fontSize = 12.sp)
                        }
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 12.dp, top = 10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        //标题
                        Text(text = stringResource(R.string.str_text),color = colorResource(R.color.color_33),fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        //位置信息
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(R.drawable.icon_home_acts_adress), contentDescription =null )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = stringResource(R.string.str_text),color = colorResource(R.color.color_66),fontSize = 12.sp,
                                overflow = TextOverflow.Ellipsis,maxLines = 1)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        //电话
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(R.mipmap.common_loc), contentDescription =null )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = stringResource(R.string.str_text),color = colorResource(R.color.color_66),fontSize = 12.sp,
                                overflow = TextOverflow.Ellipsis,maxLines = 1)
                        }
                    }
                    Spacer(modifier = Modifier.width(37.dp))
                    //位置距离
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Image(painter = painterResource(R.mipmap.car_location), contentDescription =null )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(R.string.str_awayFromYouX,""),color = colorResource(R.color.color_66),fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }

}

/**
 * 车主认证
* */
@Composable
fun OwnerCertification(dataBean: NewCarInfoBean?=null){
    Spacer(modifier = Modifier.height(18.dp))
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
            .background(Color.White, shape = RoundedCornerShape(5.dp))) {
        Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(dataBean?.pic) ?: R.mipmap.head_default,
            builder = {placeholder(R.mipmap.head_default)}),
            contentScale = ContentScale.Crop,
            contentDescription =null,modifier = Modifier
                .fillMaxWidth()
                .height(138.dp)
                .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 10.dp)) {
            Text(buildAnnotatedString {
                withStyle(style = ParagraphStyle(lineHeight = 17.sp)) {
                    withStyle(style = SpanStyle(color = colorResource(R.color.color_33),fontSize = 14.sp)) {
                        append("${stringResource(id = R.string.str_upgradeYourCarExperience)}\n")
                    }
                    withStyle(style = SpanStyle(color = colorResource(R.color.color_99),fontSize = 12.sp)) {
                        append(stringResource(id = R.string.str_bindYourCar_x))
                    }
                }
            })
            Spacer(modifier = Modifier.height(18.dp))
            Button(onClick = { },shape = RoundedCornerShape(24.dp),contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.color_00095B)),
                modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.str_toCertifyOwner),fontSize = 15.sp,color = Color.White)
            }
        }
        Divider(color = colorResource(R.color.color_E8EBF3),modifier = Modifier
            .height(0.5.dp)
            .fillMaxWidth())
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 13.dp, bottom = 14.dp, start = 14.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.str_yourCarBindingIsUnderReview_x),color = colorResource(R.color.color_00095B),fontSize = 12.sp,
            modifier = Modifier.weight(1f))
            Row(verticalAlignment=Alignment.CenterVertically,modifier = Modifier.clickable {  }) {
                Text(text = stringResource(R.string.str_look),fontSize = 12.sp,color=colorResource(R.color.color_99))
                Icon(painter = painterResource(R.mipmap.right_99) , contentDescription =null,tint = colorResource(R.color.color_99))
            }
        }
    }
}
/**
 * 预览
* */
@Preview
@Composable
private fun PreviewUI(){
    val dataList = arrayListOf<NewCarTagBean>()
    for (i in 0..10){
        dataList.add(NewCarTagBean(iconName = "Tag$i"))
    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(colorResource(R.color.color_F4))) {
        //售后服务
        AfterSalesService(NewCarInfoBean(icons = dataList))
        //寻找经销商
        LookingDealers()
        //车主认证
        OwnerCertification()
    }
}