package com.changanford.home.search.adapter

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.AskListMainData
import com.changanford.common.util.CustomImageSpan
import com.changanford.home.R
import com.changanford.home.databinding.ItemSearchResultAskBinding

class SearchAskResultAdapter(val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<AskListMainData, BaseDataBindingHolder<ItemSearchResultAskBinding>>(
        R.layout.item_search_result_ask
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemSearchResultAskBinding>,
        item: AskListMainData
    ) {
        //     val headFrameName:String="", 这里取 这些
        //    val headFrameImage:String=""
        holder.dataBinding?.let { it ->
            showZero(it.tvTitle, item)
//            showTag(it.tvTitle, item)
            it.tvGetFb.isVisible = item.fbReward > 0
            it.tvGetFb.text = item.fbReward.toString().plus("福币奖励")
            it.tvTag.text = item.questionTypeName
        }

    }


    private fun showTag(text: AppCompatTextView?, item: AskListMainData) {
        if (item.fbReward <= 0) {
            showZero(text, item)
            return
        }
        val fbNumber = item.fbReward.toString().plus("福币")
        val tagName = item.questionTypeName
        val starStr = " ".repeat(tagName.length * 3)
        val str = "$starStr\t\t\t\t${item.title} [icon] $fbNumber"
        //先设置原始文本
        text?.text = str
        //使用post方法，在TextView完成绘制流程后在消息队列中被调用
        text?.post { //获取第一行的宽度
            val stringBuilder: StringBuilder = StringBuilder(str)
            //SpannableString的构建
            val spannableString = SpannableString("$stringBuilder ")
            val drawable = ContextCompat.getDrawable(context, R.mipmap.question_fb)
            drawable?.apply {
                val imageSpan = CustomImageSpan(this)
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                val strLength = spannableString.length
                val numberLength = fbNumber.length
                val startIndex = strLength - numberLength - 1
                spannableString.setSpan(
                    AbsoluteSizeSpan(30),
                    startIndex,
                    strLength,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    ForegroundColorSpan(Color.parseColor("#E1A743")), startIndex, strLength,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    imageSpan, str.lastIndexOf("["), str.lastIndexOf("]") + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                text.text = spannableString
            }
        }
    }

    private fun showZero(text: AppCompatTextView?, item: AskListMainData) {
        val tagName = item.questionTypeName
        val starStr = " ".repeat(tagName.length * 3)
        val str = "$starStr\t\t\t\t${item.title}"
        //先设置原始文本
        text?.text = str
    }
}