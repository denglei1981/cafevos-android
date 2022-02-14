package com.changanford.circle.ui.compose

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import com.changanford.common.MyApp
import com.changanford.common.bean.QuestionInfoBean
import com.changanford.common.bean.QuestionItemBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils
import java.text.SimpleDateFormat

/**
 * @Author : wenke
 * @Time : 2022/1/24
 * @Description : QuestionCompose
 */
@SuppressLint("SimpleDateFormat")
private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
@Preview
@Composable
private fun PreviewUI(){
    ComposeQuestionTop(MyApp.mContext)
    QuestionItemUI()
}
/**
 * 暂无内容
* */
@Composable
fun EmptyCompose(noContext:String?=null){
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(colorResource(R.color.color_F4))
        .padding(90.dp, 50.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(painter = painterResource(R.mipmap.icon_common_acts_empty), contentDescription =null )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = noContext?:stringResource(R.string.str_noContent), fontSize = 11.sp,color= colorResource(R.color.color_99))
    }
}
/**
 * 缺省页-问答
* */
@Composable
fun EmptyQuestionCompose(){
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(colorResource(R.color.color_F4))
        .padding(90.dp, 50.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(painter = painterResource(R.mipmap.icon_common_acts_empty), contentDescription =null )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.empty_question), fontSize = 11.sp,color= colorResource(R.color.color_99))
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            //去提问
            JumpUtils.instans?.jump(116)
        },shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.color_00095B)),
            contentPadding= PaddingValues(4.dp),
            modifier = Modifier
                .width(82.dp)
                .height(26.dp)) {
            Text(stringResource(R.string.str_toAskQuestions),fontSize = 12.sp,color = Color.White)
        }
    }
}
/**
 * 提问按钮
* */
@Composable
fun BtnQuestionCompose(){
    Column(modifier = Modifier
        .background(color = colorResource(R.color.color_01025C), shape = CircleShape)
        .defaultMinSize(minWidth = 45.dp,minHeight = 45.dp)
        .padding(5.dp)
        .clickable {
            JumpUtils.instans?.jump(116)
        }, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painter = painterResource(id = R.mipmap.circle_edit_pb), contentDescription = null,tint = Color.White)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = stringResource(R.string.str_questions), fontSize = 10.sp, color = Color.White)
    }
}

@Composable
fun ComposeQuestionTop(context: Context, dataBean: QuestionInfoBean?=null){
    dataBean?.apply {
        val userInfo=dataBean.user
        val identityType=getIdentity()
        Box(contentAlignment = Alignment.TopCenter,modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.color_F4))) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(189.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            colorResource(R.color.color_00095B),
                            colorResource(R.color.color_222B80)
                        )
                    )
                ))
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 90.dp, start = 20.dp, end = 20.dp)) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)){
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(5.dp))
                            .padding(0.dp, 10.dp)) {
                        //昵称、标签身份
                        Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 92.dp)) {
                            Text(text = userInfo.nickName?:"",fontSize = 16.sp, color=colorResource(R.color.color_33),maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.width(5.dp))
                            //技师身份
                            if(identityType==1)Image(painter = painterResource(R.mipmap.question_crown), contentDescription =null)
                            Spacer(modifier = Modifier.width(5.dp))
                            if(identityType==2){
                                Box(contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .background(
                                            colorResource(R.color.color_1A00095B),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(7.dp, 2.dp)) {
                                    Text(text = "${userInfo.modelName} 车主",fontSize = 10.sp, color=colorResource(R.color.color_00095B),maxLines = 1,
                                        overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                        //技师自己可修改资料
                        if(isOneself()&&identityType==1){
//                            Spacer(modifier = Modifier.height(5.dp))
                            //修改资料
                            Row(modifier = Modifier
                                .padding(start = 92.dp)
                                .clickable {
                                    JumpUtils.instans?.jump(115, userInfo.conQaTechnicianId)
                                },verticalAlignment =Alignment.CenterVertically) {
                                Image(painter = painterResource(R.mipmap.question_edit), contentDescription = null)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = stringResource(R.string.str_modifyData),fontSize = 11.sp, color=colorResource(R.color.color_99))
                            }
                        }
                        if(introduction!=null){
                            Spacer(modifier = Modifier.height(18.dp))
                            //个人简介
                            Text(text = introduction?:"",fontSize = 12.sp, color=colorResource(R.color.color_99),lineHeight =17.sp,modifier = Modifier.padding(start = 18.dp,end = 18.dp))
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                        Divider(modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(colorResource(R.color.color_80EEEEEE)))
                        Spacer(modifier = Modifier.height(7.dp))
                        //答题总数、采纳总数、回复榜、采纳绑
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp, 3.dp)) {
                            getStatisticalTypes(context).apply {
                                for (i in 0 until size){
                                    val item=this@apply[i]
                                    Column(modifier = Modifier.weight(1f),horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = item.tagName?:"",fontSize = 11.sp, color=colorResource(R.color.color_99))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(buildAnnotatedString {
                                            withStyle(style = SpanStyle(color =colorResource(R.color.color_33),fontSize = 15.sp)) {
                                                withStyle(style = SpanStyle(color = colorResource(if(size==4&&i>1)R.color.color_E1A743 else R.color.color_33),fontSize = 15.sp)) {
                                                    append(item.tag?:"0")
                                                }
                                                if(size==4&&i>1)withStyle(style = SpanStyle(color = colorResource(R.color.color_99),fontSize = 10.sp)) { append(stringResource(id = R.string.str_name)) }
                                            }
                                        }, textAlign = TextAlign.Center)
                                    }
                                    Divider(modifier = Modifier
                                        .width(0.5.dp)
                                        .height(40.dp),color = colorResource( R.color.color_f6))
                                }
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
fun QuestionItemUI(itemData: QuestionItemBean?=null, viewWidthDp:Int=0,identity:Int?=null){
    itemData?.apply {
        val answerInfoBean=qaAnswer//答案 可能为null
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)) {
//            TopUI()
            Spacer(modifier = Modifier.height(21.dp))
            //图片列表
            ImgsUI(imgs,viewWidthDp)
            if(answerInfoBean==null){//无人回答
                AnswerUI(this@apply) //立即抢答
            }else{//有回答
                UserInfoUI(this@apply) //用户信息
            }
            Spacer(modifier = Modifier.height(16.dp))
            if(identity!=1){
                Divider(color = colorResource(id = R.color.color_ee), modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp))
            }
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
                            for(column in 0 until columnSize){
                                Box(modifier = Modifier
                                    .weight(1f)
                                    .height(imgSize.dp), contentAlignment = Alignment.BottomEnd){
                                    if(column<itemList.size){
                                        Image(
                                            painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(itemList[column]) ?: R.mipmap.head_default,
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
                                        //最后一张图片显示总图片数
                                        if(rowTotal-1==row&&columnSize-1==column&&size>4){
                                            Row(modifier = Modifier.padding(bottom =9.dp)) {
                                                Box(contentAlignment = Alignment.Center,modifier = Modifier
                                                    .background(
                                                        color = colorResource(R.color.color_4D000000),
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(8.dp, 1.dp)){
                                                    Text(text = "$size+",color = Color.White,fontSize = 10.sp,textAlign = TextAlign.Center)
                                                }
                                                Spacer(modifier = Modifier.width(20.dp))
                                            }
                                        }
                                    }
                                }
                                if(column!=columnSize-1)Spacer(modifier = Modifier.width(10.dp))
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
private fun AnswerUI(itemData: QuestionItemBean){
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            //立即抢答
            JumpUtils.instans?.jump(itemData.jumpType,itemData.jumpValue)
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
private fun UserInfoUI(itemData: QuestionItemBean){
    val answerInfo=itemData.qaAnswer
    val userInfo=answerInfo?.qaUserVO
    if(answerInfo==null||userInfo==null)return
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth().clickable {
                                                         JumpUtils.instans?.jump(114,userInfo.conQaUjId)
        }, verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(userInfo.avater) ?: R.mipmap.head_default,
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
                Row(verticalAlignment = Alignment.Top) {
                    //昵称
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(color = colorResource(R.color.color_99),fontSize = 13.sp)) {
                            append(userInfo.nickName ?:"")
                        }
                        userInfo.modelName?.let {
                            withStyle(style = SpanStyle(color = colorResource(R.color.color_00095B),fontSize = 11.sp)) {
                                append("   ${it}车主")
                            }
                        }
                    }, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.width(4.dp))
                    //皇冠-是技师
                    if(userInfo.conQaTechnicianId!=null)Image(painter = painterResource(R.mipmap.question_crown), contentDescription = null)
                }
//                Spacer(modifier = Modifier.height(6.dp))
                //回答时间
                answerInfo.answerTime?.let {
                    Text(text = simpleDateFormat.format(it),color= colorResource(R.color.color_99), fontSize = 11.sp, overflow = TextOverflow.Ellipsis, maxLines = 1)
                }
            }
            if(answerInfo.adopt=="YES"){//已采纳
                Box(contentAlignment = Alignment.Center,modifier = Modifier
                    .defaultMinSize(47.dp, 16.dp)
                    .background(
                        colorResource(R.color.color_1A00095B),
                        shape = RoundedCornerShape(8.dp)
                    )) {
                    Text(text = stringResource(R.string.str_hasBeenAdopted),color = colorResource(R.color.color_00095B),fontSize = 11.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        //回答内容
        Text(text = answerInfo.content?:"",color= colorResource(R.color.color_66), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            //浏览量
            Image(painter = painterResource(R.drawable.icon_circle_look_count), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = itemData.viewVal?:"0",color = colorResource(R.color.color_99),fontSize = 12.sp)
            //回答数
            Spacer(modifier = Modifier.width(21.dp))
            Image(painter = painterResource(R.drawable.icon_circle_msg_count), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text =itemData.answerCount?:"0",color = colorResource(R.color.color_99),fontSize = 12.sp)
        }
    }
}

