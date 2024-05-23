package com.changanford.circle.ui.ask.adapter

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemRecommendAskNoAnswerBinding
import com.changanford.common.bean.AskListMainData
import com.changanford.common.text.addTag
import com.changanford.common.text.addTextTag
import com.changanford.common.text.addUrlTag
import com.changanford.common.text.annotation.DrawableZoomType
import com.changanford.common.text.config.TagConfig
import com.changanford.common.text.config.Type
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.GlideUtils.loadCompress2
import com.changanford.common.utilext.toIntPx
import com.core.util.dp
import com.core.util.sp


/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
@Suppress("IMPLICIT_CAST_TO_ANY")
class RecommendAskAdapter :
    BaseQuickAdapter<AskListMainData, BaseViewHolder>(R.layout.item_recommend_ask_no_answer) {

    override fun convert(holder: BaseViewHolder, item: AskListMainData) {

        when (item.itemType) {
            0 -> {
                noAnswer(holder.itemView, item)
            }

            3 -> {

            }

            else -> {
                noAnswer(holder.itemView, item)
            }
        }

    }

    @SuppressLint("SetTextI18n", "NewApi")
    private fun showAnswer(binding: ItemRecommendAskNoAnswerBinding?, item: AskListMainData) {
        binding?.let {
            it.layoutAnswer.layoutCount.tvAskFb.isVisible = item.fbReward > 0
            it.layoutAnswer.layoutCount.tvAskFb.text =  "+${item.fbReward}"
            it.layoutAnswer.layoutCount.tvCount.text =
                "${item.answerCount}回答  ${CountUtils.formatNum(item.viewVal.toString(), false)}浏览"
            it.layoutAnswer.tvContent.isVisible = false
            item.qaAnswer?.let { answer ->
                it.layoutAnswer.apply {
                    it.layoutAnswer.layoutCount.tvAskFb.isVisible = "NO" == answer.adopt
//                    it.layoutAnswer.btnFollow.text = if ("NO" == answer.adopt) "采纳" else "已采纳"
                    tvContent.isVisible =
                        !answer.content.isNullOrEmpty() || !answer.answerContents.isNullOrEmpty()
                    tvContent.text =
                        if (!answer.content.isNullOrEmpty()) answer.content else if (!answer.answerContents.isNullOrEmpty()) {
                            answer.answerContents!![0].imgDesc
                        } else {
                            ""
                        }
                    if (tvContent.text.isEmpty()){
                        return@let
                    }
                    tvContent.addUrlTag {
                        imageUrl =
                            if (answer.qaUserVO?.avater == null) "111" else GlideUtils.handleImgUrl(
                                answer.qaUserVO.avater
                            )
                        isCircle = true
                        imageHeight = 25.dp
                        imageWidth = 25.dp
                        drawableZoomType = DrawableZoomType.CUSTOM
                        leftTopRadius = 50.dp.toFloat()
                        rightPadding = 3.dp
                        startGradientBackgroundColor = Color.parseColor("#F6D242")
                        endGradientBackgroundColor = Color.parseColor("#FF52E5")
                        position = 0
                    }.addTextTag {
                        leftPadding = 3.dp
                        rightPadding = 2.dp
                        text = " ${if (answer.qaUserVO == null) "" else answer.qaUserVO.nickName}: "
                        textColor = ContextCompat.getColor(context, R.color.color_9916)
                        backgroundColor = ContextCompat.getColor(context, R.color.white)
                    }
                }
            }
        }
    }

    private fun noAnswer(view: View, item: AskListMainData) {
        val binding = DataBindingUtil.bind<ItemRecommendAskNoAnswerBinding>(view)
        showNoQuestion(binding, item)

    }

    private fun showNoQuestion(binding: ItemRecommendAskNoAnswerBinding?, item: AskListMainData) {
        showAnswer(binding, item)
        val picList = item.getPicLists()
        if (picList?.isEmpty() == false) {
            binding?.layoutAnswer?.clPic?.isVisible = true
            when {
                picList.size > 1 -> {
                    binding?.layoutAnswer?.apply {
                        ivOnePic.isVisible = false
                        ivTwoOnePic.isVisible = true
                        ivTwoTwoPic.isVisible = true
                        ivTwoOnePic.loadCompress2(picList[0])
                        ivTwoTwoPic.loadCompress2(picList[1])
                        tvPicNum.isVisible = picList.size > 2
                        tvPicNum.text = "+${picList.size - 2}"
                    }
                }

                picList.size == 1 -> {
                    binding?.layoutAnswer?.apply {
                        ivOnePic.isVisible = true
                        ivTwoOnePic.isVisible = false
                        ivTwoTwoPic.isVisible = false
                        ivOnePic.loadCompress2(picList[0])
                        tvPicNum.isVisible = picList.size > 2
                    }
                }

                else -> {

                }
            }
        } else {
            binding?.layoutAnswer?.clPic?.isVisible = false
            binding?.layoutAnswer?.tvPicNum?.isVisible = false
        }
        binding?.layoutAskInfo?.run {
            tvTitle.text = item.title
            if (!item.questionTypeName.isNullOrEmpty()) {
                val tvConfig = TagConfig(Type.TEXT).apply {
                    text = item.questionTypeName
                    textColor = ContextCompat.getColor(context, R.color.white)
                    marginRight = 10.toIntPx()
                    backgroundColor =
                        ContextCompat.getColor(context, R.color.color_1700F4)
                    radius = 4.dp.toFloat()
                    textSize = 10.sp.toFloat()
                    topPadding = 2.dp
                    bottomPadding = 2.dp
                }
                tvTitle.addTag(tvConfig)
            }
        }
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

            val transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.bottom / 2 + top + 4
            canvas.save()
            canvas.translate(x, transY.toFloat())
            drawable.draw(canvas)
            canvas.restore()
        }
    }


}