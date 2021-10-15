package com.changanford.home.adapter

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.RecommendData
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class RecommendAdapter : BaseMultiItemQuickAdapter<RecommendData, BaseViewHolder>() {
    init {
        addItemType(1, R.layout.item_home_recommend_items_one)
        addItemType(2, R.layout.item_home_recommend_items_three)
        addItemType(3, R.layout.item_home_acts)

    }


    override fun convert(holder: BaseViewHolder, item: RecommendData) {
        val picLists = item.getPicLists()
        when (item.itemType) {
            1 -> {//1张图
                showPics(holder, item)
                val ivPic = holder.getView<ShapeableImageView>(R.id.iv_pic)
                if (!TextUtils.isEmpty(item.pic)) {
                    GlideUtils.loadBD(item.pic, ivPic,R.mipmap.image_h_one_default)
                } else if (picLists != null) {
                    GlideUtils.loadBD(picLists[0], ivPic)
                }
            }
            2 -> { //3张图
                showPics(holder, item)
                val tvPicSizes = holder.getView<AppCompatTextView>(R.id.tv_pic_size)
                item.getPicLists()?.let {
                    tvPicSizes.text = it.size.toString()
                }
                val onePic = holder.getView<ShapeableImageView>(R.id.iv_one)
                val twoPic = holder.getView<ShapeableImageView>(R.id.iv_two)
                val threePic = holder.getView<ShapeableImageView>(R.id.iv_three)
                if (picLists != null) {
                    for (s in picLists) {
                        val index = picLists.indexOf(s)
                        when (index) {
                            0 -> {
                                GlideUtils.loadBD(s, onePic)
                            }
                            1 -> {
                                GlideUtils.loadBD(s, twoPic)
                            }
                            2 -> {
                                GlideUtils.loadBD(s, threePic)
                            }
                        }
                    }
                }
            }
            3 -> { // 活动
                showActs(holder, item)
            }

        }
    }

    fun showActs(holder: BaseViewHolder, item: RecommendData) { //活动
        val ivActs = holder.getView<ShapeableImageView>(R.id.iv_acts)
        val tvTips = holder.getView<AppCompatTextView>(R.id.tv_tips)
        val tvHomeActAddress = holder.getView<AppCompatTextView>(R.id.tv_home_act_address)
        val tvHomeActTimes = holder.getView<AppCompatTextView>(R.id.tv_home_act_times)
        val btnState = holder.getView<MaterialButton>(R.id.btn_state)
        val tvTagOne = holder.getView<AppCompatTextView>(R.id.tv_tag_one)
        val tvTagTwo = holder.getView<AppCompatTextView>(R.id.tv_tag_two)
        GlideUtils.loadBD(item.wonderfulPic, ivActs)
        tvTips.text = item.title



        tvHomeActTimes.text = "活动截止时间:".plus(item.deadLineTime)
        if (item.deadLineTime <= item.serverTime) {
            btnState.text = "已截止"
        } else {
            btnState.text = "进行中"
        }
        when (item.wonderfulType) {
            0 -> {
                tvTagTwo.text = "线上活动"
                tvHomeActTimes.text =
                    "活动截止时间:".plus(TimeUtils.formateActTime(item.deadLineTime))
                tvHomeActAddress.visibility=View.GONE
            }
            1 -> {
                tvTagTwo.text = "线下活动"
                tvHomeActTimes.text =
                    "报名截止时间: ".plus(TimeUtils.MillisTo_M_H(item.deadLineTime))
                tvHomeActAddress.text = "地点：".plus(item.city)
                tvHomeActAddress.visibility=View.VISIBLE
            }
            2 -> {
                tvTagTwo.text = "调查问卷"
                tvHomeActTimes.text = ("截止时间: " + TimeUtils.MillisTo_M_H(item.deadLineTime))
                tvHomeActAddress.visibility=View.GONE
            }
            3 -> {
                tvTagTwo.text = "厂家活动"
                tvHomeActTimes.text =
                    "报名截止时间: ".plus(TimeUtils.MillisTo_M_H(item.deadLineTime))
                tvHomeActAddress.visibility=View.GONE
            }
        }
        when (item.official) {
            0 -> {
                tvTagOne.text = "官方"
                tvTagOne.visibility = View.VISIBLE
            }
            2 -> {
                tvTagOne.text = "经销商"
                tvTagOne.visibility = View.VISIBLE
            }
            else -> {
                tvTagOne.visibility = View.VISIBLE
                tvTagOne.text = "个人"
            }
        }


    }

    fun showPics(holder: BaseViewHolder, item: RecommendData) { // 图片
        val ivHeader = holder.getView<ShapeableImageView>(R.id.iv_header)
        val tvAuthorName = holder.getView<TextView>(R.id.tv_author_name)
        val tvSubtitle = holder.getView<TextView>(R.id.tv_sub_title)
        GlideUtils.loadBD(item.authors?.avatar, ivHeader)
        tvAuthorName.text = item.authors?.nickname
        tvSubtitle.text = item.authors?.memberName
        val tvContent = holder.getView<TextView>(R.id.tv_content)
        val btnFollow = holder.getView<MaterialButton>(R.id.btn_follow)

        val tvNewsTag = holder.getView<TextView>(R.id.tv_news_tag)

        val tvVideoTime=holder.getView<TextView>(R.id.tv_video_times)

        ivHeader.setOnClickListener {
            toUserHomePage(item)
        }
        tvAuthorName.setOnClickListener {
            toUserHomePage(item)
        }

        tvContent.text = item.getContent()
        val tvLikeCount = holder.getView<TextView>(R.id.tv_like_count)
        val tvCommentCount = holder.getView<TextView>(R.id.tv_comment_count)
        val tvTimeAndViewCount = holder.getView<TextView>(R.id.tv_time_look_count)
        tvLikeCount.text = item.getLikeCount()
        tvCommentCount.text = item.getCommentCount()
        tvTimeAndViewCount.text = item.getTimeAdnViewCount()
        val tvTopic = holder.getView<TextView>(R.id.tv_topic)
        if (TextUtils.isEmpty(item.getTopic())) {
            tvTopic.text = ""
        } else {
            tvTopic.text = "#${item.getTopic()}#"
        }
        when (item.authors?.isFollow) {
            0 -> { // 未关注
                btnFollow.text = "关注"
                btnFollow.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_gray_f2f4f9
                    )
                )
                btnFollow.setTextColor(ContextCompat.getColor(context, R.color.blue_tab))
            }
            1 -> {//  已经关注
                btnFollow.text = "已关注"
                btnFollow.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                btnFollow.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }


        val rvUserTag=holder.getView<RecyclerView>(R.id.rv_user_tag)
        if (item.authors != null) {
            val labelAdapter = LabelAdapter(16)
            rvUserTag.adapter=labelAdapter
            labelAdapter.setNewInstance(item.authors?.imags)
        }
        when (item.rtype) {
            1 -> {
                tvNewsTag.visibility = View.VISIBLE
                if(!TextUtils.isEmpty(item.artVideoTime)){
                    tvVideoTime.text=item.artVideoTime
                }
                tvVideoTime.visibility=View.VISIBLE
            }
            else -> {
                tvNewsTag.visibility = View.GONE
                tvVideoTime.visibility=View.GONE
            }

        }
    }

    private fun toUserHomePage(item: RecommendData) {
        JumpUtils.instans!!.jump(35, item.authors?.userId.toString())
    }
}
