package com.changanford.circle.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.changanford.circle.R
import com.changanford.common.bean.QuestionInfoBean
import com.changanford.common.bean.QuestionItemBean
import com.changanford.common.utilext.GlideUtils

/**
 * @Author : wenke
 * @Time : 2022/1/24 0024
 * @Description : QuestionCompose
 */
@Preview
@Composable
private fun PreviewUI(){
    ComposeQuestionTop()
    QuestionItemUI()
}
@Composable
fun ComposeQuestionTop(dataBean: QuestionInfoBean?=null){
    dataBean?.apply {
        val userInfo=dataBean.user
        Box(contentAlignment = Alignment.TopCenter,modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.color_F4))) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(189.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(colorResource(R.color.color_00095B), colorResource(R.color.color_222B80))
                    )
                ))
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 90.dp, start = 20.dp, end = 20.dp)) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)){
                    Column(
                        Modifier.fillMaxWidth().background(Color.White, shape = RoundedCornerShape(5.dp))
                            .padding(0.dp, 10.dp)) {
                        //昵称、标签身份
                        Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 92.dp)) {
                            Text(text = userInfo.nickName?:"",fontSize = 16.sp, color=colorResource(R.color.color_33),maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.width(5.dp))
                            Image(painter = painterResource(R.mipmap.question_crown), contentDescription =null)
                            Spacer(modifier = Modifier.width(5.dp))
                            Box(contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .background(
                                        colorResource(R.color.color_1A00095B),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(7.dp, 2.dp)) {
                                Text(text = "新一代福克斯 车主",fontSize = 10.sp, color=colorResource(R.color.color_00095B),maxLines = 1,
                                    overflow = TextOverflow.Ellipsis)
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        //修改资料
                        Row(modifier = Modifier
                            .padding(start = 92.dp)
                            .clickable {
                            },verticalAlignment =Alignment.CenterVertically) {
                            Image(painter = painterResource(R.mipmap.question_edit), contentDescription = null)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = stringResource(R.string.str_modifyData),fontSize = 11.sp, color=colorResource(R.color.color_99))
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        //个人简介
                        Text(text = introduction?:"",fontSize = 12.sp, color=colorResource(R.color.color_99),lineHeight =17.sp,modifier = Modifier.padding(start = 18.dp,end = 18.dp))
                        Spacer(modifier = Modifier.height(28.dp))
                        Divider(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(colorResource(R.color.color_80EEEEEE)))
                        Spacer(modifier = Modifier.height(7.dp))
                        //答题总数、采纳总数、回复榜、采纳绑
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp, 3.dp)) {
                            val titles= arrayListOf(R.string.str_totalNumberQuestions,R.string.str_adoptTotal,R.string.str_replyToList,R.string.str_adoptionList)
                            for (i in 0..3){
                                Column(modifier = Modifier.weight(1f),horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = stringResource(titles[i]),fontSize = 11.sp, color=colorResource(R.color.color_99))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(buildAnnotatedString {
                                        withStyle(style = SpanStyle(color =colorResource(R.color.color_33),fontSize = 15.sp)) {
                                            withStyle(style = SpanStyle(color = colorResource(if(i>1)R.color.color_E1A743 else R.color.color_33),fontSize = 15.sp)) {
                                                append("0")
                                            }
                                            if(i>1)withStyle(style = SpanStyle(color = colorResource(R.color.color_99),fontSize = 10.sp)) { append(stringResource(id = R.string.str_name)) }
                                        }
                                    }, textAlign = TextAlign.Center)
                                }
                                Divider(modifier = Modifier.width(0.5.dp).height(40.dp),color = colorResource( R.color.color_f6))
                            }
                        }
                    }
                }
                Row {
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(userInfo.avater) ?: R.mipmap.head_default,
                            builder = {
                                placeholder(R.mipmap.head_default)
                            }),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(69.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }
    }

}

@Composable
fun QuestionItemUI(itemData: QuestionItemBean?=null, viewWidthDp:Int=0){
    itemData?.apply {
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)) {
//            TopUI()
            Spacer(modifier = Modifier.height(21.dp))
            //图片列表
            ImgsUI(imgs,viewWidthDp)
            //立即抢答
            AnswerUI()
            //用户信息
            UserInfoUI()
            Spacer(modifier = Modifier.height(15.dp))
            Divider(color = colorResource(id = R.color.color_ee), modifier = Modifier.fillMaxWidth().height(0.5.dp))
        }
    }
}
@Composable
private fun TopUI(){
    Box {
        Text(buildAnnotatedString {
            withStyle(style = ParagraphStyle(lineHeight = 20.sp)) {
                withStyle(style = SpanStyle(color = Color.Transparent,fontSize = 15.sp)) {
                    append(stringResource(R.string.str_vehicleFailure)+"\t")
                }
                withStyle(style = SpanStyle(color = colorResource(R.color.color_2d),fontSize = 15.sp)) {
                    append("福克斯 穿越千年的丝绸古道，感叹".repeat(2))
                }
                withStyle(style = SpanStyle(color = colorResource(R.color.color_E1A743),fontSize = 10.sp)) {
                    append("\t30福币")
                }
            }
        })
        Box(contentAlignment = Alignment.Center, modifier = Modifier
            .border(
                0.5.dp,
                color = colorResource(R.color.color_00095B),
                shape = RoundedCornerShape(2.dp)
            )
            .padding(5.dp, 2.dp)
            .background(color = Color.White)){
            Text(text = stringResource(R.string.str_vehicleFailure), color = colorResource(R.color.color_00095B), fontSize = 12.sp)
        }
    }
}
/**
 * img
 *[viewWidthDp]view宽度
* */
@Composable
private fun ImgsUI(imgs:String?,viewWidthDp:Int=0){
    imgs?.split(",")?.filter { it!="" }?.apply {
        when (size) {
            0->{}
            1 -> {
                val pic=this[0]
                val imgSize=viewWidthDp*0.49
                Image(
                    painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(pic) ?: R.mipmap.head_default,
                        builder = {
                            
                            placeholder(R.mipmap.head_default)
                        }),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imgSize.dp)
                        .clip(RoundedCornerShape(5.dp))
                )
            }
            2,3 -> {
                val imgSize=(viewWidthDp-((size-1)*10))/size
                Row(modifier = Modifier.fillMaxWidth()) {
                    for(i in 0 until size){
                        Image(
                            painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(this@apply[i]) ?: R.mipmap.head_default,
                                builder = {
                                    
                                    placeholder(R.mipmap.head_default)
                                }),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .size(imgSize.dp)
                                .clip(RoundedCornerShape(5.dp))
                        )
                        if(i!=size-1)Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
            else-> {//大于等于4
                val imgSize=(viewWidthDp-10)/2
                Column(Modifier.fillMaxWidth()) {
                    val rowTotal=2//总共几排
                    val columnSize=2//一排几列
                    for(row in 0 until rowTotal){
                        val startIndex=row*columnSize
                        val endIndex=if(row!=rowTotal-1)(row+1)*columnSize else size
                        val itemList=slice(startIndex until endIndex)
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for(i in 0 until columnSize){
                                Image(
                                    painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(itemList[i]) ?: R.mipmap.head_default,
                                        builder = {
                                            placeholder(R.mipmap.head_default)
                                        }),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(imgSize.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                )
                                if(i!=columnSize-1)Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                        if(row!=rowTotal-1)Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}
/**
 * 立即抢答
* */
@Composable
private fun AnswerUI(){
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            //立即抢答
        },shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.color_00095B)),
            contentPadding= PaddingValues(4.dp),
            modifier = Modifier
                .width(87.dp)
                .height(29.dp)) {
            Text(stringResource(R.string.str_immediatelyViesToAnswerFirst),fontSize = 13.sp,color = Color.White)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = stringResource(R.string.str_questionHasNotBeenAnswered),color= colorResource(R.color.color_99), fontSize = 10.sp)
    }
}
/**
 * 用户信息
 * */
@Composable
private fun UserInfoUI(){
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = rememberImagePainter(data = GlideUtils.handleNullableUrl("") ?: R.mipmap.head_default,
                    builder = {
                        placeholder(R.mipmap.head_default)
                    }),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    //昵称
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(color = colorResource(R.color.color_99),fontSize = 13.sp)) {
                            append("xxxx")
                        }
                        withStyle(style = SpanStyle(color = colorResource(R.color.color_00095B),fontSize = 11.sp)) {
                            append("   EVOS车主")
                        }
                    }, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    //皇冠
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(painter = painterResource(R.mipmap.question_crown), contentDescription = null)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "2021-08-12 15:30 ",color= colorResource(R.color.color_99), fontSize = 11.sp, overflow = TextOverflow.Ellipsis, maxLines = 1)
            }
            //已采纳
            Box(contentAlignment = Alignment.Center,modifier = Modifier
                .defaultMinSize(47.dp, 16.dp)
                .background(
                    colorResource(R.color.color_1A00095B),
                    shape = RoundedCornerShape(8.dp)
                )) {
                Text(text = stringResource(R.string.str_hasBeenAdopted),color = colorResource(R.color.color_00095B),fontSize = 11.sp)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        //内容
        Text(text = "xxx",color= colorResource(R.color.color_66), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            //浏览
            Image(painter = painterResource(R.drawable.icon_circle_look_count), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "0",color = colorResource(R.color.color_99),fontSize = 12.sp)
            //评论
            Spacer(modifier = Modifier.width(21.dp))
            Image(painter = painterResource(R.drawable.icon_circle_msg_count), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text ="0",color = colorResource(R.color.color_99),fontSize = 12.sp)
        }
    }
}

