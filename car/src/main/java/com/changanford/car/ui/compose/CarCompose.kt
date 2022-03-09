package com.changanford.car.ui.compose

import androidx.compose.foundation.BorderStroke
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
import com.changanford.common.bean.CarAuthBean
import com.changanford.common.bean.CarItemBean
import com.changanford.common.bean.NewCarInfoBean
import com.changanford.common.bean.NewCarTagBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
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
    val dataList=carInfoBean?.icons
    if(dataList == null|| dataList.isEmpty())return
    //一排几列
    val columnSize=3
    //总共几排
    val rowTotal= WCommonUtil.getHeatNumUP("${dataList.size/columnSize.toFloat()}",0).toInt()
    Spacer(modifier = Modifier.height(18.dp))
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
                        Box(modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp, end = 10.dp)) {
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
        Column(horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier
            .fillMaxWidth()
            .clickable {
                WBuriedUtil.clickCarAfterSalesService(iconName)
                JumpUtils.instans?.jump(jumpDataType, jumpDataValue)
            }) {
           Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(iconImg) ?: R.mipmap.head_default,
               builder = {placeholder(R.mipmap.head_default)}),
               contentScale = ContentScale.Crop,
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
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
            )
            .padding(end = 15.dp, top = 10.dp, bottom = 15.dp)) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier
                .weight(1f)
                .clickable {
                    JumpUtils.instans?.jump(1, MConstant.H5_CAR_DEALER)
                }) {
                //标题
                Text(text = dealerName?:"",color = colorResource(R.color.color_33),fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                //位置信息
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.mipmap.car_location_small), contentDescription =null )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = address?:"",color = colorResource(R.color.color_66),fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis,maxLines = 1)
                }
                Spacer(modifier = Modifier.height(10.dp))
                //电话
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.mipmap.car_phone), contentDescription =null )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = phone?:"",color = colorResource(R.color.color_66),fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis,maxLines = 1)
                }
            }
            Spacer(modifier = Modifier.width(37.dp))
            //位置距离 导航
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {
                WBuriedUtil.clickCarAfterSalesNavigate()
                JumpUtils.instans?.jump(69,"{\"lngX\": \"${lngX}\",\"latY\": \"${latY}\",\"name\": \"$dealerName\"}")
            }) {
                Spacer(modifier = Modifier.height(10.dp))
                Image(painter = painterResource(R.mipmap.car_location), contentDescription =null )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.str_awayFromYouX,distanct?:""),color = colorResource(R.color.color_66),fontSize = 12.sp)
            }
        }

    }

}
/**
 * 车主认证-未认证
 * */
@Composable
fun OwnerCertificationUnauthorized(dataBean: NewCarInfoBean?=null,isUse:Boolean=true,carAuthBean: CarAuthBean?=null){
    carAuthBean?.carAuthConfVo?.apply {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)) {
            Box(modifier = Modifier
                .padding(bottom = 5.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp)), contentAlignment = Alignment.BottomEnd){
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 18.dp, end = 0.dp, bottom = 29.dp)) {
                    Text(text = title?:stringResource(R.string.str_upgradeYourCarExperience), fontSize = 24.sp, color = colorResource(R.color.color_33))
                    Spacer(modifier = Modifier.height(9.dp))
                    Text(text =des?:stringResource( R.string.str_bindYourCar_x), fontSize = 13.sp, color = colorResource(R.color.color_66),modifier = Modifier.width(180.dp))
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(onClick = {
                        WBuriedUtil.clickCarCertification()
                        //去做认证
                        JumpUtils.instans?.jump(17,dataBean?.modelCode)
                    },enabled = isUse,shape = RoundedCornerShape(24.dp), border = BorderStroke(width = 1.dp,
                        colorResource(if(isUse)R.color.color_00095B else R.color.color_DD)),contentPadding = PaddingValues(10.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = if(isUse)Color.White else colorResource(R.color.color_DD)),
                        modifier = Modifier.width(96.dp)) {
                        Text(stringResource(R.string.str_goToCertification),fontSize = 15.sp,color =  colorResource(if(isUse)R.color.color_00095B else R.color.colorWhite))
                    }
                }
                Column(modifier = Modifier.padding(bottom = 24.dp, end = 20.dp, start = 140.dp)) {
                    img?.apply {
                        Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(this) ?: R.mipmap.head_default,
                            builder = {placeholder(R.mipmap.head_default)}),
                            contentScale = ContentScale.Crop,
                            contentDescription =null,modifier = Modifier
                                .height(72.dp)
                                .clip(RoundedCornerShape(5.dp)))
                    }
                }
            }
        }
    }
}
/**
 * 车主认证
* */
@Composable
fun OwnerCertification(dataBean: NewCarInfoBean?=null,isUse:Boolean=true,carAuthBean: CarAuthBean?=null,carItemBean: CarItemBean?=null){
    if(carItemBean==null){//未认证
        OwnerCertificationUnauthorized(dataBean,isUse,carAuthBean)
        return
    }
    val carAuthConfVo=carAuthBean?.carAuthConfVo
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
            .background(Color.White, shape = RoundedCornerShape(5.dp))) {
        carAuthConfVo?.img?.apply {
            Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(this) ?: R.mipmap.head_default,
                builder = {placeholder(R.mipmap.head_default)}),
                contentScale = ContentScale.Crop,
                contentDescription =null,modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)))
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 10.dp)) {
            Text(buildAnnotatedString {
                withStyle(style = ParagraphStyle(lineHeight = 17.sp)) {
                    withStyle(style = SpanStyle(color = colorResource(R.color.color_33),fontSize = 14.sp)) {
                        append((carAuthConfVo?.title?:stringResource(R.string.str_upgradeYourCarExperience))+"\n")
                    }
                    withStyle(style = SpanStyle(color = colorResource(R.color.color_99),fontSize = 12.sp)) {
                        append(carAuthConfVo?.des?:stringResource( R.string.str_bindYourCar_x))
                    }
                }
            })
            Spacer(modifier = Modifier.height(18.dp))
            Button(onClick = {
                WBuriedUtil.clickCarCertification()
                //去做认证
                JumpUtils.instans?.jump(17,dataBean?.modelCode)
            },enabled = isUse,shape = RoundedCornerShape(24.dp),contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(if(isUse)R.color.color_00095B else R.color.color_DD)),
                modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.str_toCertifyOwner),fontSize = 15.sp,color = Color.White)
            }
        }
        //待审核
        if(carItemBean!=null&&carItemBean.authStatus<3){
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
                Row(verticalAlignment=Alignment.CenterVertically,modifier = Modifier.clickable {
                    WBuriedUtil.clickCarExamined()
                    //查看审核记录
                    JumpUtils.instans?.jump(41,carItemBean.authId)
                }) {
                    Text(text = stringResource(R.string.str_look),fontSize = 12.sp,color=colorResource(R.color.color_99))
                    Icon(painter = painterResource(R.mipmap.right_99) , contentDescription =null,tint = colorResource(R.color.color_99))
                }
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