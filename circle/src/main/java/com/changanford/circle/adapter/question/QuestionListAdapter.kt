package com.changanford.circle.adapter.question

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemQuestionBinding
import com.changanford.circle.ui.ask.adapter.RecommendAskAdapter
import com.changanford.circle.ui.compose.QuestionItemUI
import com.changanford.common.bean.QuestionItemBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.wutil.ScreenUtils
import kotlin.math.floor


class QuestionListAdapter(val activity:Activity,var identity:Int?=null): BaseQuickAdapter<QuestionItemBean, BaseDataBindingHolder<ItemQuestionBinding>>(
    R.layout.item_question){
    private val viewWidth by lazy { ScreenUtils.getScreenWidthDp(context)-60 }
    private val dp12 by lazy { ScreenUtils.dp2px(context,12f) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemQuestionBinding>, itemData: QuestionItemBean) {
        holder.dataBinding?.apply {
            val position=holder.absoluteAdapterPosition
//            WCommonUtil.setMargin(layoutRoot,0,0,0,if(identity!=1)0 else dp12)
            layoutRoot.apply {
                if(data.size==1)
                    setBackgroundResource(R.drawable.circle_white_5_bg)
                else{
                    when (position) {
                        0 -> setBackgroundResource(R.drawable.shape_white_t_5dp)
                        data.size-1 -> setBackgroundResource(R.drawable.shape_white_b_5dp)
                        else -> setBackgroundResource(R.color.colorWhite)
                    }
                }
            }
            val fbReward=itemData.fbReward
            val tagName=itemData.questionTypeName
            if(fbReward==null||fbReward=="0"){
                val tagLength= tagName.length
                val starStr=" ".repeat(tagLength*(if(tagLength<2)5 else 3))
                val str="$starStr\t\t\t\t${itemData.title}"
                tvTitle.text=str
            }else showTag(tvTitle,itemData)
//            else setTxt(context,tvTitle,"$str    $fbNumber",fbNumber)
            tvTag.text=tagName
            composeView.setContent {
                QuestionItemUI(itemData,viewWidth,identity,position==data.size-1)
            }
            root.setOnClickListener { JumpUtils.instans?.jump(itemData.jumpType,itemData.jumpValue) }
        }
    }
   private fun showTag(text: AppCompatTextView?, item: QuestionItemBean){
        val fbNumber=item.fbReward.plus("福币")
        val tagName=item.questionTypeName
        val tagLength= tagName.length
        val starStr=" ".repeat(tagLength*(if(tagLength<2)5 else 3))
        val str="$starStr\t\t\t\t${item.title} [icon] $fbNumber"
        //先设置原始文本
        text?.text=str
        //使用post方法，在TextView完成绘制流程后在消息队列中被调用
        text?.post { //获取第一行的宽度
            val stringBuilder: StringBuilder =StringBuilder(str)
            //SpannableString的构建
            val spannableString = SpannableString("$stringBuilder ")
            val drawable = ContextCompat.getDrawable(context,R.mipmap.question_fb)
            drawable?.apply {
                val imageSpan = RecommendAskAdapter.CustomImageSpan(this)
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                val strLength=spannableString.length
                val numberLength=fbNumber.length
                val startIndex=strLength-numberLength-1
                spannableString.setSpan(AbsoluteSizeSpan(30), startIndex, strLength,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#E1A743")),startIndex,strLength,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(imageSpan,str.lastIndexOf("["),str.lastIndexOf("]")+1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                text.text = spannableString
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
//            val stringBuilder: StringBuilder =StringBuilder(str).insert(numberPerLine - 1, " ")
            val stringBuilder: StringBuilder =if(str.length<=numberPerLine)StringBuilder(str) else StringBuilder(str).insert(numberPerLine - 1, " ")
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
