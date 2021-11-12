package com.changanford.home.adapter

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.RecommendData
import com.changanford.common.net.*
import com.changanford.common.util.CountUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.api.HomeNetWork
import com.changanford.home.util.LoginUtil
import com.changanford.home.util.launchWithCatch
import com.changanford.home.widget.DrawCenterTextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class RecommendAdapter(var lifecycleOwner: LifecycleOwner) :
    BaseMultiItemQuickAdapter<RecommendData, BaseViewHolder>() {
    init {
        addItemType(0, R.layout.item_home_recommend_items_one)
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
                    GlideUtils.loadBD(item.pic, ivPic, R.mipmap.image_h_one_default)
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
        tvHomeActTimes.text = "活动截止时间:".plus(item.getEndStr())

        btnState.text = item.getTimeStateStr()

        when (item.wonderfulType) {
            0 -> {
                tvTagTwo.text = "线上活动"
                tvHomeActTimes.text =
                    "活动截止时间:".plus(TimeUtils.formateActTime(item.getEndStr()))
                tvHomeActAddress.visibility = View.GONE
            }
            1 -> {
                tvTagTwo.text = "线下活动"
                tvHomeActTimes.text =
                    "报名截止时间: ".plus(TimeUtils.MillisTo_M_H(item.getEndStr()))
                tvHomeActAddress.text = "地点：".plus(item.city)
                tvHomeActAddress.visibility = View.VISIBLE
            }
            2 -> {
                tvTagTwo.text = "调查问卷"
                tvHomeActTimes.text = ("截止时间: " + TimeUtils.MillisTo_M_H(item.getEndStr()))
                tvHomeActAddress.visibility = View.GONE
            }
            3 -> {
                tvTagTwo.text = "厂家活动"
                tvHomeActTimes.text =
                    "报名截止时间: ".plus(TimeUtils.MillisTo_M_H(item.getEndStr()))
                tvHomeActAddress.visibility = View.GONE
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
        tvSubtitle.text = item.authors?.getMemberNames()
        val tvContent = holder.getView<TextView>(R.id.tv_content)
        val btnFollow = holder.getView<MaterialButton>(R.id.btn_follow)

        val tvNewsTag = holder.getView<TextView>(R.id.tv_news_tag)

        val tvVideoTime = holder.getView<TextView>(R.id.tv_video_times)
        val ivPlay = holder.getView<ImageView>(R.id.iv_play)

        ivHeader.setOnClickListener {
            toUserHomePage(item)
        }
        tvAuthorName.setOnClickListener {
            toUserHomePage(item)
        }
        if(TextUtils.isEmpty(item.getTopic())){
            tvContent.visibility=View.GONE
        }else{
            tvContent.visibility=View.VISIBLE
            tvContent.text = item.getTopic()
        }


        val tvLikeCount = holder.getView<DrawCenterTextView>(R.id.tv_like_count)
        setLikeState(tvLikeCount, item.isLike, false) // 设置是否喜欢。
        tvLikeCount.setOnClickListener {
            when (item.rtype) {
                1 -> { // 点赞资讯。
                    if (LoginUtil.isLongAndBindPhone()) {
                        if (item.authors != null) {
                            if (item.isLike == 0) {
                                item.isLike = 1
                                val likesCount = item.likeCount.plus(1)
                                item.likeCount = likesCount
                                tvLikeCount.setPageTitleText(
                                    CountUtils.formatNum(
                                        likesCount.toString(),
                                        false
                                    ).toString()
                                )
                            } else {
                                item.isLike = 0
                                val likesCount = item.likeCount.minus(1)
                                item.likeCount = likesCount
                                tvLikeCount.setPageTitleText(
                                    CountUtils.formatNum(
                                        likesCount.toString(),
                                        false
                                    ).toString()
                                )
                            }
                            actionLike(item.artId)
                            setLikeState(tvLikeCount, item.isLike, true)
                        }
                    }
                }
                2 -> {// 点赞帖子
                    if (LoginUtil.isLongAndBindPhone()) {
                        likePost(tvLikeCount, item)
                    }
                }
            }
        }
        val tvCommentCount = holder.getView<TextView>(R.id.tv_comment_count)
        val tvTimeAndViewCount = holder.getView<TextView>(R.id.tv_time_look_count)
        tvLikeCount.setPageTitleText(item.getLikeCount())
        tvCommentCount.text = item.getCommentCount()
        tvTimeAndViewCount.text = item.getTimeAdnViewCount()
        val tvTopic = holder.getView<TextView>(R.id.tv_topic)
        if (TextUtils.isEmpty(item.getContent())) {
            tvTopic.text = ""
            tvTopic.visibility=View.GONE
        } else {
            tvTopic.visibility=View.VISIBLE
            tvTopic.text = item.getContent()
        }
        item.authors?.let {
            setFollowState(btnFollow, it)
        }

        btnFollow.setOnClickListener {
            // 判断是否登录。
            if (LoginUtil.isLongAndBindPhone()) {
                if (item.authors != null) {
                    followAction(btnFollow, item.authors!!, holder.adapterPosition)
                }
            }
        }
        val rvUserTag = holder.getView<RecyclerView>(R.id.rv_user_tag)
        if (item.authors != null) {
            val labelAdapter = LabelAdapter(16)
            rvUserTag.adapter = labelAdapter
            labelAdapter.setNewInstance(item.authors?.imags)
        }
        when (item.rtype) {
            1 -> {
                tvNewsTag.visibility = View.VISIBLE
                if (!TextUtils.isEmpty(item.artVideoTime)) {
                    tvVideoTime.text = item.artVideoTime
                }

                tvVideoTime.visibility = View.VISIBLE
                tvNewsTag.text = "资讯"
                ivPlay.visibility = if (item.isArtVideoType()) View.VISIBLE else View.GONE
                tvVideoTime.visibility = if (item.isArtVideoType()) View.VISIBLE else View.GONE
            }
            else -> {
                tvNewsTag.visibility = View.GONE
                tvVideoTime.visibility = View.GONE
                ivPlay.visibility = View.GONE
            }

        }
    }

    private fun toUserHomePage(item: RecommendData) {
        JumpUtils.instans!!.jump(35, item.authors?.authorId.toString())
    }

    /**
     *  设置关注状态。
     * */
    fun setFollowState(btnFollow: MaterialButton, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(context)
        authors.let {
            setFollowState.setFollowState(btnFollow, it, true)
        }
    }

    // 关注或者取消
    private fun followAction(btnFollow: MaterialButton, authorBaseVo: AuthorBaseVo, position: Int) {
        var followType = authorBaseVo.isFollow
        followType = if (followType == 1) 2 else 1
//        authorBaseVo.isFollow = followType
        getFollow(authorBaseVo.authorId, followType)
    }

    // 关注。
    private fun getFollow(followId: String, type: Int) {
        lifecycleOwner.launchWithCatch {
            val requestBody = HashMap<String, Any>()
            requestBody["followId"] = followId
            requestBody["type"] = type
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .followOrCancelUser(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    notifyAtt(followId, type)
                }.onWithMsgFailure {
                    it?.let { it1 -> toastShow(it1) }
                }
        }
    }

    //关注
    fun notifyAtt(userId: String, isFollow: Int) {
        for (data in this.data) {
            if (data.authors?.authorId == userId) {
                data.authors?.isFollow = isFollow
            }
        }
        this.notifyDataSetChanged()
    }

    // 喜欢
    fun actionLike(artId: String) {
        lifecycleOwner.launchWithCatch {
            val requestBody = HashMap<String, Any>()
            requestBody["artId"] = artId
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .actionLike(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {

                }.onWithMsgFailure {
                    it?.let { it1 -> toastShow(it1) }
                }
        }
    }

    fun setLikeState(tvLikeView: DrawCenterTextView, isLike: Int, isAnim: Boolean) {
        if (isLike == 0) {
            tvLikeView.setThumb(R.drawable.icon_big_shot_unlike, isAnim)
        } else {
            tvLikeView.setThumb(R.mipmap.home_comment_like, isAnim)
        }
    }


    private fun likePost(tvLikeView: DrawCenterTextView, item: RecommendData) {
        val activity = BaseApplication.curActivity as AppCompatActivity
        activity.launchWithCatch {
            val body = MyApp.mContext.createHashMap()
            body["postsId"] = item.postsId
            val rKey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .actionPostLike(body.header(rKey), body.body(rKey)).also {
                    if (it.code == 0) {
                        it.msg.toast()
                        if (item.isLike == 0) {
//                            "点赞成功".toast()
                            item.isLike = 1
                            tvLikeView.setThumb(R.mipmap.home_comment_like, true)
                            item.postsLikesCount++
                        } else {
//                            "取消点赞".toast()
                            item.isLike = 0
                            item.postsLikesCount--
                            tvLikeView.setThumb(R.drawable.icon_big_shot_unlike, false)
                        }
                        tvLikeView.setPageTitleText(item.getLikeCount())
                    } else {
                        it.msg.toast()
                    }
                }
        }
    }


}
