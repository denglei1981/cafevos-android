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
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemQuestionBinding
import com.changanford.circle.ui.compose.QuestionItemUI
import com.changanford.common.bean.QuestionInfoBean
import com.changanford.common.wutil.ScreenUtils
import kotlin.math.floor


class QuestionListAdapter(val activity:Activity): BaseQuickAdapter<QuestionInfoBean, BaseDataBindingHolder<ItemQuestionBinding>>(
    R.layout.item_question){
    private val viewWidth by lazy { ScreenUtils.getScreenWidthDp(context)-60 }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemQuestionBinding>, itemData: QuestionInfoBean) {
        val fbNumber="603福币"
        val tagName="车辆故障"
        val starStr=" ".repeat(tagName.length*3)
        val str="$starStr   福克斯 穿越千年的丝绸古道，感叹福克斯 穿越千年的丝绸古道，感叹    $fbNumber"
        holder.dataBinding?.apply {
//            WCommonUtil.htmlToImgStr(activity,tvTitle,"$str<img src=\"${R.mipmap.question_fb}\"/>" +
//                    "<font color=\"#E1A743\"><myfont size='30px'>20</myfont></font>","myfont")
            setTxt(context,tvTitle,str,fbNumber)
            tvTag.text=tagName
            composeView.setContent {
                QuestionItemUI(itemData,viewWidth)
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
//                val endIndex=strLength
                spannableString.setSpan(AbsoluteSizeSpan(30), startIndex, strLength,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(android.graphics.Color.parseColor("#E1A743")),startIndex,strLength,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(imageSpan,startIndex-3,startIndex-1,Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                text.text = spannableString
            }
        }
    }
}
