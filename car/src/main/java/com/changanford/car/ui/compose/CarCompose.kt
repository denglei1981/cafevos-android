package com.changanford.car.ui.compose

import android.text.TextUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.changanford.car.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.*
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterCarControlPath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.wutil.WCommonUtil
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2022/1/20 0020
 * @Description : CarCompose
 */

/**
 * 售后服务
 * */
@Composable
fun AfterSalesService(carInfoBean: NewCarInfoBean?) {
    val dataList = carInfoBean?.icons
    if (dataList.isNullOrEmpty()) return
    //一排几列
    val columnSize = 3
    //总共几排
    val rowTotal = WCommonUtil.getHeatNumUP("${dataList.size / columnSize.toFloat()}", 0).toInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        Text(
            text = carInfoBean.modelName,
            color = colorResource(R.color.color_d916),
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(5.dp))
        ) {
            Spacer(modifier = Modifier.height(25.dp))
            for (row in 0 until rowTotal) {
                val startIndex = row * columnSize
                val endIndex = if (row != rowTotal - 1) (row + 1) * columnSize else dataList.size
                val itemList = dataList.slice(startIndex until endIndex)
                val itemListSize = itemList.size
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (i in 0 until columnSize) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp, end = 10.dp)
                        ) {
                            ItemService(
                                if (itemListSize > i) itemList[i] else null,
                                (i + 1) + (row * 3)
                            )
                        }
                        if (i < 2) Spacer(modifier = Modifier.width(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(15.dp))
}

/**
 * 售后服务item
 * */
@Composable
private fun ItemService(itemData: NewCarTagBean?, position: Int) {
    itemData?.apply {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxWidth()
            .clickable {
                WBuriedUtil.clickCarAfterSalesService(iconName)
                GIOUtils.homePageClick("车主服务", (position).toString(), iconName)
                JumpUtils.instans?.jump(jumpDataType, jumpDataValue)
            }) {
            Image(
                painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(iconImg)
                    ?: R.mipmap.head_default,
                    builder = { placeholder(R.mipmap.head_default) }),
                contentScale = ContentScale.Crop,
                contentDescription = null, modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = iconName ?: "",
                fontSize = 12.sp,
                color = colorResource(R.color.color_33),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }

    }
}

/**
 * 寻找经销商
 * */
@Composable
fun LookingDealers(dataBean: NewCarInfoBean? = null,carModelId:String) {
    dataBean?.apply {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
                )
                .padding(end = 15.dp, top = 10.dp, bottom = 15.dp)
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier
                .weight(1f)
                .clickable {
                    JumpUtils.instans?.jump(1, MConstant.H5_CAR_DEALER)
                }) {
                //标题
                Text(
                    text = dealerName ?: "",
                    color = colorResource(R.color.color_d916),
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    //位置信息
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            JumpUtils.instans?.jump(
                                69,
                                "{\"lngX\": \"${lngX}\",\"latY\": \"${latY}\",\"name\": \"$dealerName\"}"
                            )
                        }) {
                        Image(
                            painter = painterResource(R.mipmap.car_location_small),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${distanct}km" ?: "",
                            color = colorResource(R.color.color_8016),
                            fontSize = 12.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    //电话
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                        MineUtils.callPhone(BaseApplication.curActivity, phone)
                    }) {
                        Image(
                            painter = painterResource(R.mipmap.car_phone),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = phone ?: "",
                            color = colorResource(R.color.color_8016),
                            fontSize = 12.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.width(37.dp))
            //位置距离 导航
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    WBuriedUtil.clickCarAfterSalesNavigate()
//                    JumpUtils.instans?.jump(jumpDataType, jumpDataValue)
//                    JumpUtils.instans?.jump(
//                        69,
//                        "{\"lngX\": \"${lngX}\",\"latY\": \"${latY}\",\"name\": \"$dealerName\"}"
//                    )
                    JumpUtils.instans?.jump(1, "${MConstant.H5_BOOKING_TEST_DRIVE}${MConstant.carBannerCarModelId}")
                }) {
                Text(
                    text = "预约试驾",
                    color = colorResource(R.color.color_1700f4),
                    fontSize = 16.sp
                )
            }
        }

    }

}

/**
 * 车主认证-未认证
 * */
@Composable
fun OwnerCertificationUnauthorized(
    dataBean: NewCarInfoBean? = null,
    isUse: Boolean = true,
    carAuthBean: CarAuthBean? = null,
    carItemBean: CarItemBean? = null
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    carAuthBean?.carAuthConfVo?.apply {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier.padding(bottom = 5.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 18.dp, end = 0.dp, bottom = 29.dp)
                    ) {
                        Text(
                            text = title ?: stringResource(R.string.str_upgradeYourCarExperience),
                            fontSize = 24.sp,
                            color = colorResource(R.color.color_33)
                        )
                        Spacer(modifier = Modifier.height(9.dp))
                        Text(
                            text = des ?: stringResource(R.string.str_bindYourCar_x),
                            fontSize = 13.sp,
                            color = colorResource(R.color.color_66),
                            modifier = Modifier.width(180.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = {
                                WBuriedUtil.clickCarCertification()
                                GIOUtils.homePageClick("车主认证", 1.toString(), "去认证")
                                //去做认证
                                JumpUtils.instans?.jump(17, dataBean?.modelCode)
                            },
                            enabled = isUse,
                            elevation = null,
                            interactionSource = interactionSource,
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                colorResource(if (isUse) R.color.color_1700f4 else R.color.color_DD)
                            ),
                            contentPadding = PaddingValues(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (isUse) Color.White else colorResource(
                                    R.color.color_DD
                                )
                            ),
                            modifier = Modifier.width(96.dp)
                        ) {
                            Text(
                                stringResource(R.string.str_goToCertification),
                                fontSize = 15.sp,
                                color = colorResource(if (isUse) R.color.color_1700f4 else R.color.colorWhite)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.padding(
                            bottom = 24.dp,
                            end = 20.dp,
                            start = 140.dp
                        )
                    ) {
                        img?.apply {
                            var urlStr = GlideUtils.handleNullableUrl(this)
                            Image(
                                painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(
                                    this
                                ) ?: R.mipmap.head_default,
                                    builder = { placeholder(R.mipmap.head_default) }),
                                contentScale = ContentScale.Crop,
                                contentDescription = null, modifier = Modifier
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(5.dp))
                            )
                        }
                    }
                }
                AuditPromptCompose(carItemBean)
            }
        }
    }
}

/**
 * 审核中
 * */
@Composable
private fun AuditPromptCompose(carItemBean: CarItemBean? = null) {
    if (carItemBean != null && carItemBean.authStatus < 3) {
        Divider(
            color = colorResource(R.color.color_E8EBF3), modifier = Modifier
                .height(0.5.dp)
                .fillMaxWidth()
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 13.dp, bottom = 14.dp, start = 14.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.str_yourCarBindingIsUnderReview_x),
                color = colorResource(R.color.color_1700f4),
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                WBuriedUtil.clickCarExamined()
                //查看审核记录
                JumpUtils.instans?.jump(41, carItemBean.authId)
            }) {
                Text(
                    text = stringResource(R.string.str_look),
                    fontSize = 12.sp,
                    color = colorResource(R.color.color_99)
                )
                Icon(
                    painter = painterResource(R.mipmap.right_99),
                    contentDescription = null,
                    tint = colorResource(R.color.color_99)
                )
            }
        }
    }
}

/**
 * 已认证
 * */
@Composable
fun CarAuthLayout(carItemBean: CarItemBean, auditBean: CarItemBean? = null) {
    MaterialTheme {
        val interactionSource = remember {
            MutableInteractionSource()
        }
        Card(
            elevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp)
                .defaultMinSize(minHeight = dimensionResource(id = R.dimen.dp_170))
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(5.dp)
                )
                .clickable(interactionSource = interactionSource, indication = null) {
                    val jumpValue =
                        "{\"vin\":\"${carItemBean.vin}\",\"status\":${carItemBean.authStatus}}"
                    JumpUtils.instans?.jump(41, jumpValue)
                }
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "VIN码：${carItemBean.vin}",
                        fontSize = 13.sp,
                        color = colorResource(id = R.color.text_colorv6),
                        modifier = Modifier
                            .weight(1.0f)
                            .padding(start = 18.dp, top = 14.dp, bottom = 14.dp, end = 14.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(77.dp, 27.dp)
                            .background(
                                color = Color(0x691700f4),
                                shape = RoundedCornerShape(0.dp, 5.dp, 0.dp, 5.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "默认", fontSize = 13.sp, color = Color.White)
                    }
                }
                Divider(
                    color = colorResource(id = R.color.color_ee),
                    modifier = Modifier.height(0.5.dp)
                )
                Row(modifier = Modifier.padding(16.dp, 21.dp, 0.dp, 0.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = carItemBean.seriesName,
                            color = colorResource(R.color.color_00142E),
                            fontSize = 15.sp
                        )
                        Box(
                            modifier = Modifier
                                .offset(y = dimensionResource(id = R.dimen.dp_10))
                                .background(
                                    color = if (carItemBean.plateNum?.isEmpty() == true || "无牌照" == carItemBean.plateNum) {
                                        Color(0xff1700f4)
                                    } else {
                                        Color(0x201700f4)
                                    },
                                    shape = RoundedCornerShape(17.dp)
                                )
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    carItemBean.plateNum?.let {
                                        RouterManger
                                            .param("value", carItemBean.carSalesInfoId)
                                            .param("plateNum", it)
                                            .param("authId", carItemBean.authId)

                                            .startARouter(ARouterMyPath.AddCardNumTransparentUI)
                                    }
                                }
                                .padding(horizontal = dimensionResource(id = R.dimen.dp_5))

                        ) {
                            if (TextUtils.isEmpty(carItemBean.plateNum) || "无牌照" == carItemBean.plateNum) Text(
                                text = "添加车牌",
                                fontSize = 14.sp,
                                color = Color.White,
                                modifier = Modifier.padding(5.dp)
                            ) else carItemBean.plateNum?.let {
                                Text(
                                    text = it,
                                    fontSize = 15.sp,
                                    color = Color(0xff1700f4),
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }
                    }
                    Image(
                        modifier = Modifier
                            .size(width = 182.dp, height = 106.dp)
                            .offset(
                                -dimensionResource(id = R.dimen.dp_4),
                                -dimensionResource(id = R.dimen.dp_5)
                            ),
                        painter =
                        rememberImagePainter(data = GlideUtils.handleNullableUrl(carItemBean.modelUrl)
                            ?: R.mipmap.ic_car_auth_ex,
                            builder = {
                                crossfade(true)
                                placeholder(R.mipmap.ic_car_auth_ex)
                            }),
                        contentDescription = ""
                    )
                }
                AuditPromptCompose(auditBean)
            }
        }
    }
}

/**
 * 车主认证
 * */
@Composable
fun OwnerCertification(
    dataBean: NewCarInfoBean? = null,
    isUse: Boolean = true,
    carAuthBean: CarAuthBean? = null,
    carItemBean: CarItemBean? = null
) {
    if (carItemBean == null) {//未认证
        OwnerCertificationUnauthorized(dataBean, isUse, carAuthBean, carItemBean)
        return
    }
    val carAuthConfVo = carAuthBean?.carAuthConfVo
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
            .background(Color.White, shape = RoundedCornerShape(5.dp))
    ) {
        carAuthConfVo?.img?.apply {
            Image(
                painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(this)
                    ?: R.mipmap.head_default,
                    builder = { placeholder(R.mipmap.head_default) }),
                contentScale = ContentScale.Crop,
                contentDescription = null, modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 10.dp)
        ) {
            Text(buildAnnotatedString {
                withStyle(style = ParagraphStyle(lineHeight = 17.sp)) {
                    withStyle(
                        style = SpanStyle(
//                            color = colorResource(R.color.color_33),
                            color = Color(0xFF333333),
                            fontSize = 14.sp
                        )
                    ) {
//                        append(
                        (carAuthConfVo?.title
//                                ?: stringResource(R.string.str_upgradeYourCarExperience)) + "\n"
                            ?: "升级您的用车体验") + "\n"
//                        )
                    }
                    withStyle(
                        style = SpanStyle(
//                            color = colorResource(R.color.color_99),
                            color = Color(0xFF999999),
                            fontSize = 12.sp
                        )
                    ) {
//                        append(carAuthConfVo?.des ?: stringResource(R.string.str_bindYourCar_x))
                    }
                }
            })
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = {
                    WBuriedUtil.clickCarCertification()
                    //去做认证
                    JumpUtils.instans?.jump(17, dataBean?.modelCode)
                },
                enabled = isUse,
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(if (isUse) R.color.color_1700f4 else R.color.color_DD)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.str_toCertifyOwner),
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
        //待审核
        if (carItemBean != null && carItemBean.authStatus < 3) {
            Divider(
                color = colorResource(R.color.color_E8EBF3), modifier = Modifier
                    .height(0.5.dp)
                    .fillMaxWidth()
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 13.dp, bottom = 14.dp, start = 14.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.str_yourCarBindingIsUnderReview_x),
                    color = colorResource(R.color.color_1700f4),
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                    WBuriedUtil.clickCarExamined()
                    //查看审核记录
                    JumpUtils.instans?.jump(41, carItemBean.authId)
                }) {
                    Text(
                        text = stringResource(R.string.str_look),
                        fontSize = 12.sp,
                        color = colorResource(R.color.color_99)
                    )
                    Icon(
                        painter = painterResource(R.mipmap.right_99),
                        contentDescription = null,
                        tint = colorResource(R.color.color_99)
                    )
                }
            }
        }
    }
}

/**
 * 预览
 * */
//@Preview
//@Composable
//private fun PreviewUI() {
//    val dataList = arrayListOf<NewCarTagBean>()
//    for (i in 0..10) {
//        dataList.add(NewCarTagBean(iconName = "Tag$i"))
//    }
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(colorResource(R.color.color_F4))
//    ) {
//        //售后服务
//        AfterSalesService(NewCarInfoBean(icons = dataList))
//        //寻找经销商
//        LookingDealers()
//        //车主认证
//        OwnerCertification()
//    }
//}

@Composable
fun loveCarActivityList(arrayList: ArrayList<ActivityListBean>) {
    Column() {
        Row(
            modifier =
            Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clickable {
                    startARouter(ARouterCarControlPath.LoveCarActivityAll)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "推荐活动", style = TextStyle(fontSize = 17.sp))
            Image(
                painter = rememberImagePainter(data = R.mipmap.right_black),
                contentDescription = "",
                Modifier.size(20.dp)
            )
        }
        arrayList.forEach() {
            loveCarActivityItem(it)
        }
    }
}

@Composable
fun loveCarActivityItem(activityListBean: ActivityListBean) {
    Image(
        painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(activityListBean.coverImg)),
        contentDescription = "",
        modifier =
        Modifier
            .padding(vertical = 5.dp, horizontal = 20.dp)
            .height(height = 140.dp)
            .fillMaxWidth(1f)
            .clip(
                RoundedCornerShape(5.dp)
            )
            .clickable {
                JumpUtils.instans?.jump(
                    1,
                    "${MConstant.H5_CAR_ACTIVITY}${activityListBean.activityId}"
                )
            },
        contentScale = ContentScale.FillBounds
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun loveCarActivityAll(arrayList: ArrayList<LoveCarActivityListBean>) {
    var pageState = rememberPagerState(pageCount = arrayList.size, initialPage = 0)
    var coroutineScope = rememberCoroutineScope()
    Column {
        ScrollableTabRow(
            selectedTabIndex = pageState.currentPage,
            backgroundColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pageState.currentPage]),
                    color = Color.White
                )
            }, divider = {
                TabRowDefaults.Divider(color = Color.White)
            }) {
            arrayList.forEachIndexed { index, it ->
                Tab(selected = pageState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pageState.animateScrollToPage(index)
                        }
                    }, text = {
                        Text(
                            maxLines = 1,
                            modifier = Modifier,
                            text = it.carSeriesName,
                            fontSize = if (pageState.currentPage == index) 20.sp else 16.sp
                        )
                    }, selectedContentColor = colorResource(id = R.color.color_1700f4),
                    unselectedContentColor = colorResource(id = R.color.color_99)
                )

            }
        }
        HorizontalPager(state = pageState) { index ->
            Column() {
                arrayList[index].activityList.forEach {
                    loveCarActivityItem(activityListBean = it)
                }
            }
        }
    }

}
