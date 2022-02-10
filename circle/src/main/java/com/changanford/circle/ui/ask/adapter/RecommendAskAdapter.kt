package com.changanford.circle.ui.ask.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
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
        binding?.layoutAskInfo?.tvTitle?.text = item.content
        val picList = item.getPicLists()
        if (picList?.isEmpty() == false) {
            when {
                picList.size > 1 -> {
                    val imageInfoList: ArrayList<ImageInfo> = arrayListOf()
                    picList.forEach {
                        val imageInfo = ImageInfo()
                        imageInfo.bigImageUrl = it
                        imageInfo.thumbnailUrl = it
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
        binding?.layoutAskInfo?.tvTitle?.text = item.content
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

    fun showTag(tvTag: AppCompatTextView?, item: AskListMainData){


        var titleAndTag="车辆故障"+item.title

        val tagBg =  ContextCompat.getDrawable(context,R.drawable.shap_00095_shader)

        val spannableString = SpannableString(titleAndTag)

        spannableString.setSpan(ImageSpan(tagBg!!),0,3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableString.setSpan(object : ImageSpan(tagBg) {
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
                paint.typeface = Typeface.create("normal", Typeface.BOLD)
                paint.textSize = 50f
                val len = paint.measureText(text, start, end).roundToInt()
                drawable.setBounds(0, 0, len, 60)
                super.draw(canvas, text, start, end, x, top, y, bottom, paint)
                paint.color = Color.BLUE
                paint.typeface = Typeface.create("normal", Typeface.BOLD)
                paint.textSize = 40f
                canvas.drawText(text.subSequence(start, end).toString(), x + 10, y.toFloat(), paint)
            }
        }, 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


//        Drawable bg = getResources().getDrawable(R.drawable.text_background);
//        msp.setSpan(new ImageSpan(bg) {
//            @Override
//            public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
//                int bottom, Paint paint) {
//                paint.setTypeface(Typeface.create("normal", Typeface.BOLD));
//                paint.setTextSize(50);
//                int len = Math.round(paint.measureText(text, start, end));
//                getDrawable().setBounds(0, 0, len, 60);
//                super.draw(canvas, text, start, end, x, top, y, bottom, paint);
//                paint.setColor(Color.BLUE);
//                paint.setTypeface(Typeface.create("normal", Typeface.BOLD));
//                paint.setTextSize(40);
//                canvas.drawText(text.subSequence(start, end).toString(), x + 10, y, paint);
//            }
//        }, 57, 59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvTag?.text = spannableString

    }

}