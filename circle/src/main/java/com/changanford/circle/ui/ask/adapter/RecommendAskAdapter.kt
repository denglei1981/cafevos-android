package com.changanford.circle.ui.ask.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.AskListMainData
import com.changanford.circle.bean.MultiBean
import com.changanford.circle.databinding.ItemRecommendAskAnswerPicBinding
import com.changanford.circle.databinding.ItemRecommendAskNoAnswerBinding
import com.changanford.circle.widget.assninegridview.AssNineGridViewAskClickAdapter
import com.changanford.circle.widget.assninegridview.AssNineGridViewClickAdapter
import com.changanford.circle.widget.assninegridview.ImageInfo
import com.changanford.common.util.SpannableStringUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toastShow
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class RecommendAskAdapter : BaseMultiItemQuickAdapter<AskListMainData, BaseViewHolder>() {


    init {
        addItemType(0, R.layout.item_recommend_ask_no_answer)  //默认选择模块
        addItemType(1, R.layout.item_recommend_ask_answer_pic)
        addItemType(2, R.layout.item_recommend_ask_no_answer)
        addItemType(3,R.layout.empty_ask)
    }

    override fun convert(holder: BaseViewHolder, item: AskListMainData) {

        when (item.itemType) {
            0 -> {
                noAnswer(holder.itemView,item)
            }
            1 -> {
                //有答案。
                hasAnswer(holder.itemView, item)
            }
            3->{

            }
            else -> {
                noAnswer(holder.itemView, item)
            }
        }

    }

    fun hasAnswer(view: View, item: AskListMainData) { // 有答案
        val binding = DataBindingUtil.bind<ItemRecommendAskAnswerPicBinding>(view)
        showQuestion(binding, item)
    }

    private fun showQuestion(binding: ItemRecommendAskAnswerPicBinding?, item: AskListMainData) {
        binding?.layoutAskInfo?.tvTag?.text=item.questionTypeName
        showTag(binding?.layoutAskInfo?.tvTitle,item)
        val picList = item.getPicLists()
        if (picList?.isEmpty() == false) {
            when {
                picList.size > 1 -> {
                    val imageInfoList: ArrayList<ImageInfo> = arrayListOf()
                    picList.forEach {
                        val imageInfo = ImageInfo()
                        imageInfo.bigImageUrl = it
                        imageInfo.thumbnailUrl = it
                        imageInfo.jumpType=item.jumpType
                        imageInfo.jumpValue=item.jumpValue
                        imageInfoList.add(imageInfo)
                    }
                    val assNineAdapter = AssNineGridViewAskClickAdapter(context, imageInfoList)
                    binding?.layoutAskInfo?.ivNine?.setAdapter(assNineAdapter)
                    binding?.layoutAskInfo?.ivNine?.visibility = View.VISIBLE

                    if (picList.size > 4) {
                        binding?.layoutAskInfo?.btnMore?.visibility = View.VISIBLE
                        binding?.layoutAskInfo?.btnMore?.text = "+".plus(picList.size)
                    } else {
                        binding?.layoutAskInfo?.btnMore?.visibility = View.GONE
                    }
                }
                picList.size == 1 -> {
                    binding?.layoutAskInfo?.ivNine?.visibility = View.GONE
                    GlideUtils.loadBD(picList[0], binding?.layoutAskInfo?.ivPic!!)
                    binding.layoutAskInfo.btnMore.visibility = View.GONE
                }
                else -> {
                    binding?.layoutAskInfo?.ivNine?.visibility = View.GONE
                    binding?.layoutAskInfo?.btnMore?.visibility = View.GONE
                }
            }
        }
    }


    fun noAnswer(view: View, item: AskListMainData) {
        val binding = DataBindingUtil.bind<ItemRecommendAskNoAnswerBinding>(view)
        showNoQuestion(binding, item)

    }

    fun showNoQuestion(binding: ItemRecommendAskNoAnswerBinding?, item: AskListMainData) {
        binding?.layoutAskInfo?.tvTag?.text=item.questionTypeName
        showTag(binding?.layoutAskInfo?.tvTitle,item)
        val picList = item.getPicLists()
        if (picList?.isEmpty() == false) {
            when {
                picList.size > 1 -> {
                    val imageInfoList: ArrayList<ImageInfo> = arrayListOf()
                    picList.forEach {
                        val imageInfo = ImageInfo()
                        imageInfo.bigImageUrl = it
                        imageInfo.thumbnailUrl = it
                        imageInfo.jumpType=item.jumpType
                        imageInfo.jumpValue=item.jumpValue
                        imageInfoList.add(imageInfo)
                    }
                    val assNineAdapter = AssNineGridViewAskClickAdapter(context, imageInfoList)
                    binding?.layoutAskInfo?.ivNine?.setAdapter(assNineAdapter)
                    binding?.layoutAskInfo?.ivNine?.visibility = View.VISIBLE

                    if (picList.size > 4) {
                        binding?.layoutAskInfo?.btnMore?.visibility = View.VISIBLE
                        binding?.layoutAskInfo?.btnMore?.text = "+".plus(picList.size)
                    } else {
                        binding?.layoutAskInfo?.btnMore?.visibility = View.GONE
                    }
                }
                picList.size == 1 -> {
                    binding?.layoutAskInfo?.ivNine?.visibility = View.GONE
                    GlideUtils.loadBD(picList[0], binding?.layoutAskInfo?.ivPic!!)
                    binding.layoutAskInfo.btnMore.visibility = View.GONE
                }
                else -> {
                    binding?.layoutAskInfo?.ivNine?.visibility = View.GONE
                    binding?.layoutAskInfo?.btnMore?.visibility = View.GONE
                }
            }
        }
    }

    fun showTag(text: AppCompatTextView?, item: AskListMainData){
        if(item.fbReward<=0){
            showZero(text ,item)
            return
        }
        val fbNumber=item.fbReward.toString().plus("福币")
        val tagName=item.questionTypeName
        val starStr=" ".repeat(tagName.length*3)
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
                val imageSpan = CustomImageSpan(this)
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

    fun showZero(text: AppCompatTextView?, item: AskListMainData){
        val tagName=item.questionTypeName
        val starStr=" ".repeat(tagName.length*3)
        val str="$starStr\t\t\t\t${item.title}"
        //先设置原始文本
        text?.text=str
    }

    /**
     * 自定义imageSpan实现图片与文字的居中对齐
     */
    internal class CustomImageSpan(drawable: Drawable?) : ImageSpan(drawable!!) {
        override fun draw(
            canvas: Canvas,
            text: CharSequence,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
            val fm = paint.fontMetricsInt
            val drawable = drawable

            val transY = (y + fm.descent + y + fm.ascent) / 2-drawable.bounds.bottom / 2 + 2
            canvas.save()
            canvas.translate(x, transY.toFloat())
            drawable.draw(canvas)
            canvas.restore()
        }
    }

}