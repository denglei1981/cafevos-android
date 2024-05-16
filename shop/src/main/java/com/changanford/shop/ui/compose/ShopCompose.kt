package com.changanford.shop.ui.compose

import android.os.Bundle
import android.text.TextUtils
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import com.changanford.common.MyApp
import com.changanford.common.R
import com.changanford.common.bean.GioPreBean
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.gio.updateGoodsDetails
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.widget.webview.HHtmlUtils
import com.changanford.common.wutil.ScreenUtils
import com.changanford.common.wutil.WCommonUtil
import com.changanford.shop.ui.goods.GoodsDetailsActivity

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
fun HomeMyIntegralCompose(fbNumber: String? = null) {
    //是否登录
    val isLogin = !TextUtils.isEmpty(MConstant.token)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(R.color.color_081700f4),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Text(
                    text = "福币在手 精品我有", color = colorResource(id = R.color.color_16),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(5.dp))
                Row(
                    horizontalArrangement = Arrangement.Start, modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Text(
                        text = stringResource(
                            R.string.str_myFbX2
                        ),
                        color = colorResource(R.color.color_9916),
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.mipmap.ic_shop_fb),
                        contentDescription = null
                    )
                    Text(
                        text = if (isLogin) "$fbNumber" else "0",
                        fontSize = 14.sp,
                        color = colorResource(
                            id = R.color.color_c19f68
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (isLogin) {
                        WBuriedUtil.clickShopIntegral()
                        JumpUtils.instans?.jump(16)
                        GIOUtils.homePageClick("我的福币", 0.toString(), "我的福币")
                    } else JumpUtils.instans?.jump(100)
                },
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.color_01025C)),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    stringResource(if (isLogin) R.string.str_earnMoney else R.string.str_loginToView),
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * 商品推荐列表
 * */
@Composable
fun RecommendListCompose(dataBean: MutableList<GoodsItemBean>?) {
    if (dataBean == null) return
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(dataBean.size) { position ->
                RecommendItemCompose(position, dataBean[position])
            }
        }
    }

}

/**
 * 推荐列表item
 * */
@Composable
fun RecommendItemCompose(position: Int, itemData: GoodsItemBean?) {
    itemData?.apply {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp)
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                val bundle = Bundle()
                bundle.putString("spuId", spuId)
                bundle.putParcelable(
                    GioPageConstant.shopPreBean,
                    GioPreBean("推荐榜单页", "推荐榜单页")
                )
                startARouter(ARouterShopPath.ShopGoodsActivity, bundle)
//                GoodsDetailsActivity.start(spuId)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            //排名
            Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                if (position < 0) {
                    Image(
                        painter = painterResource(
                            when (position) {
                                0 -> R.mipmap.ic_ranking_1
                                1 -> R.mipmap.ic_ranking_2
                                else -> R.mipmap.ic_ranking_3
                            }
                        ), contentDescription = null
                    )
                } else {
                    Text(
                        text = "${position + 1}",
                        color = colorResource(R.color.color_D1D2D7),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            //封面
            Image(
                painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(getImgPath())
                    ?: R.mipmap.head_default,
                    builder = { placeholder(R.mipmap.head_default) }),
                contentScale = ContentScale.Crop,
                contentDescription = null, modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(5.dp))
            )
            Spacer(modifier = Modifier.width(11.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                //标题
                Text(
                    text = spuName,
                    color = colorResource(R.color.color_16),
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                //福币价格,销量
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = colorResource(R.color.color_16),
                                fontSize = 16.sp
                            )
                        ) {
                            append("¥${getRMB(priceFb)} ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = colorResource(R.color.color_66),
                                fontSize = 10.sp
                            )
                        ) {
                            append(stringResource(R.string.str_since))
                        }
                    })
                    Text(
                        text = stringResource(
                            com.changanford.shop.R.string.str_hasChangeXa,
                            "$salesCount"
                        ), color = colorResource(R.color.color_8016), fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/**
 * 商品详情-逛一逛
 * */
@Composable
fun DetailsWalkCompose(dataBean: MutableList<GoodsItemBean>? = null) {
    dataBean?.apply {
        //一排几列
        val columnSize = 3
        //总共几排
        val rowTotal =
            WCommonUtil.getHeatNumUP("${dataBean.size / columnSize.toFloat()}", 0).toInt()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(color = colorResource(R.color.color_F4))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(com.changanford.shop.R.string.str_walk),
                color = colorResource(R.color.color_16),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            for (row in 0 until rowTotal) {
                val startIndex = row * columnSize
                val endIndex = if (row != rowTotal - 1) (row + 1) * columnSize else dataBean.size
                val itemList = dataBean.slice(startIndex until endIndex)
                val itemListSize = itemList.size
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    for (i in 0 until columnSize) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            ItemDetailsWalkCompose(if (itemListSize > i) itemList[i] else null)
                        }
                        if (i < 2) Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(25.dp))
            }
        }
    }
}

/**
 * 逛一逛的item
 */
private val walkItemWidth by lazy { (ScreenUtils.getScreenWidthDp(MyApp.mContext) - 48) / 3 }

@Composable
private fun ItemDetailsWalkCompose(itemData: GoodsItemBean? = null) {
    itemData?.apply {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                updateGoodsDetails(itemData.spuName, "逛一逛")
                GoodsDetailsActivity.start(mallMallSpuId)
            }) {
            //封面
            Image(
                painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(getImgPath())
                    ?: R.mipmap.head_default,
                    builder = { placeholder(R.mipmap.head_default) }),
                contentScale = ContentScale.Crop,
                contentDescription = null, modifier = Modifier
                    .size(walkItemWidth.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))
            //商品名称
            Text(
                text = spuName,
                color = colorResource(R.color.color_16),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(5.dp))
            //价格
            Text(buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = colorResource(R.color.color_1700F4),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                ) {
                    append("¥${getRMB(vipFb)} ")
                }
                withStyle(
                    style = SpanStyle(
                        color = colorResource(R.color.color_1700F4),
                        fontSize = 10.sp
                    )
                ) {
                    append(stringResource(R.string.str_since))
                }
            })
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = stringResource(com.changanford.shop.R.string.str_hasChangeXa, "$salesCount"),
                color = colorResource(R.color.color_4d16), fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ShopServiceDescription(urlPath: String?) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
//            .animateContentSize(
//                animationSpec = spring(
//                    dampingRatio = Spring.DampingRatioMediumBouncy,
//                    stiffness = Spring.StiffnessLow
//                )
//            )
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(color = colorResource(R.color.color_F4))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, bottom = if (expanded) 8.dp else 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "服务说明",
                color = colorResource(id = R.color.color_33),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = rememberImagePainter(data = R.mipmap.ic_good_service),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 20.dp)
                    .clickable {
                        expanded = !expanded
                    }
                    .graphicsLayer {
                        rotationZ = if (expanded) 180f else 0f
                    }
                    .size(15.dp)
            )
        }
        if (expanded) {
            Surface(modifier = Modifier.padding(start = 13.dp, end = 13.dp)) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            loadDataWithBaseURL(
                                null,
                                urlPath?.let { HHtmlUtils.getHtmlData(it, "0") } ?: "",
                                "text/html",
                                "utf-8",
                                null
                            )
                        }
                    }
                )
            }
        }
    }
}