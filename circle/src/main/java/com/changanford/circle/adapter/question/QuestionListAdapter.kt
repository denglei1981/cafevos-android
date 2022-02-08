package com.changanford.circle.adapter.question

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemQuestionBinding
import com.changanford.common.bean.QuestionInfoBean
import kotlin.math.floor


class QuestionListAdapter(val activity:Activity): BaseQuickAdapter<QuestionInfoBean, BaseDataBindingHolder<ItemQuestionBinding>>(
    R.layout.item_question){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemQuestionBinding>, item: QuestionInfoBean) {
        val fbNumber="603福币"
        val tagName="车辆故障"
        var starStr=" ".repeat(tagName.length*3)
        val str="$starStr  福克斯 穿越千年的丝绸古道，感叹福克斯 穿越千年的丝绸古道，感叹    $fbNumber"
        holder.dataBinding?.apply {
//            WCommonUtil.htmlToImgStr(activity,tvTitle,"$str<img src=\"${R.mipmap.question_fb}\"/>" +
//                    "<font color=\"#E1A743\"><myfont size='30px'>20</myfont></font>","myfont")
            setTxt(context,tvTitle,str,fbNumber)
            tvTag.text=tagName
            composeView.setContent {
                ItemUI()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setTxt(context: Context, text:TextView, str:String, fbNumber:String){
        //先设置原始文本
        text.text=str
        //使用post方法，在TextView完成绘制流程后在消息队列中被调用
        text.post { //获取第一行的宽度
            val lineWidth = text.layout.getLineWidth(0)
            //获取第一行最后一个字符的下标
            val lineEnd = text.layout.getLineEnd(0)
            //计算每个字符占的宽度
            val widthPerChar = lineWidth / (lineEnd + 1)
            //计算TextView一行能够放下多少个字符
            val numberPerLine = floor((text.width / widthPerChar).toDouble()).toInt()
            //在原始字符串中插入一个空格，插入的位置为numberPerLine - 1
            val stringBuilder: StringBuilder =StringBuilder(str).insert(numberPerLine - 1, " ")
            //SpannableString的构建
            val spannableString = SpannableString("$stringBuilder ")
            val drawable = ContextCompat.getDrawable(context,R.mipmap.question_fb)
            drawable?.apply {
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                val imageSpan = ImageSpan(this, ImageSpan.ALIGN_BASELINE)
                val strLength=spannableString.length
                val numberLength=fbNumber.length
                val startIndex=strLength-numberLength-1
                val endIndex=strLength
                spannableString.setSpan(AbsoluteSizeSpan(30), startIndex, endIndex,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(android.graphics.Color.parseColor("#E1A743")),startIndex,endIndex,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(imageSpan,startIndex-3,startIndex-1,Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                text.text = spannableString
            }
        }
    }
}
@Composable
fun ItemUI(itemData: QuestionInfoBean?=null){
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)) {
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
}
@Preview
@Composable
fun PreviewUI(){
    ItemUI()
}