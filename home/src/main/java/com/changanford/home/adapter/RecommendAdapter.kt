package com.changanford.home.adapter

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.RecommendData
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.databinding.ItemHomeActsBinding
import com.changanford.common.net.*
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.util.*
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.GlideUtils.loadCompress
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
import com.xiaomi.push.it

class RecommendAdapter(var lifecycleOwner: LifecycleOwner) :
    BaseMultiItemQuickAdapter<RecommendData, BaseViewHolder>(),LoadMoreModule {
    init {
        addItemType(0, R.layout.item_home_recommend_items_one)
        addItemType(1, R.layout.item_home_recommend_items_one)
        addItemType(2, R.layout.item_home_recommend_items_three)
//        addItemType(3, R.layout.item_home_recommend_acts)
        addItemType(3, com.changanford.common.R.layout.item_home_acts)
        loadMoreModule.preLoadNumber = 10
    }


    override fun convert(holder: BaseViewHolder, item: RecommendData) {
        val picLists = item.getPicLists()
        when (item.itemType) {
            1 -> {//1张图
                showPics(holder, item)
                val veryPostIv = holder.getView<ImageView>(R.id.iv_very_post)
                item.postsIsGood?.let { g ->
                    if (g == 1) {
                        veryPostIv.visibility = View.VISIBLE
                    } else {
                        veryPostIv.visibility = View.GONE
                    }
                }
                if (item.postsIsGood == null) {
                    veryPostIv.visibility = View.GONE
                }
                val ivPic = holder.getView<ShapeableImageView>(R.id.iv_pic)
                if (!TextUtils.isEmpty(item.pic)) {
                    ivPic.loadCompress(item.pic)
                } else if (picLists != null) {
                    ivPic.loadCompress(picLists[0])
                }
            }
            2 -> { //3张图
                showPics(holder, item)
                val veryPostIv = holder.getView<ImageView>(R.id.ic_mult_very_post)
                item.postsIsGood?.let { g ->
                    if (g == 1) {
                        veryPostIv.visibility = View.VISIBLE
                    } else {
                        veryPostIv.visibility = View.GONE
                    }
                }
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
                                onePic.loadCompress(s)
                            }
                            1 -> {
                                twoPic.loadCompress(s)
                            }
                            2 -> {
                                threePic.loadCompress(s)
                            }
                        }
                    }
                }
            }
            3 -> { // 活动
//                showActs(holder, item)
                try {
                    showActsNew(holder, item)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    fun showActsNew(holder: BaseViewHolder, recdate: RecommendData) { //活动
        val item = recdate.wonderful
        val binding = DataBindingUtil.bind<ItemHomeActsBinding>(holder.itemView)
        binding?.let {
            it.ivActs.loadCompress(item.coverImg)
//            it.root.setOnClickListener {
//                JumpUtils.instans?.jump(item.jumpDto.jumpCode,item.jumpDto.jumpVal)
//            }
            it.root.setPadding(
                DisplayUtil.dip2px(BaseApplication.INSTANT, 12f),
                0,
                DisplayUtil.dip2px(BaseApplication.INSTANT, 12f),
                DisplayUtil.dip2px(BaseApplication.INSTANT, 12f)
            )
            it.root.background = null
            it.tvTips.text = item.title
            it.tvHomeActTimes.text = item.getActTimeS()
            it.btnState.isVisible = !item.activityTag.isNullOrEmpty()
            it.btnState.text = item.showTag()
            it.tvHomeActAddress.isVisible = !item.activityAddr.isNullOrEmpty()
            it.tvHomeActAddress.text = item.getAddress()
            it.tvSignpeople.isVisible = !item.activityTotalCount.isNullOrEmpty()
            it.tvSignpeopleImg.isVisible = !item.activityTotalCount.isNullOrEmpty()
            it.tvSignpeople.text = "${item.activityJoinCount}人参与"
            it.bt.isVisible = item.showButton()
            if (item.showButton()) {
                it.bt.text = item.showButtonText()
            }
            if (item.buttonBgEnable()) {
                it.bt.background =
                    BaseApplication.curActivity.resources.getDrawable(com.changanford.common.R.drawable.bg_f2f4f9_cor14)
                it.bt.setTextColor(BaseApplication.curActivity.resources.getColor(com.changanford.common.R.color.color_95b))
            } else {
                it.bt.background =
                    BaseApplication.curActivity.resources.getDrawable(com.changanford.common.R.drawable.bg_dd_cor14)
                it.bt.setTextColor(BaseApplication.curActivity.resources.getColor(com.changanford.common.R.color.white))
            }
            it.bt.setOnClickListener {
                if (item.isFinish()) {
                    AlertThreeFilletDialog(BaseApplication.curActivity).builder()
                        .setMsg(
                            "一旦结束将无法恢复，确定结束吗？"
                        )
                        .setCancelable(true)
                        .setPositiveButton("确定", com.changanford.common.R.color.color_01025C) {
                        }
                        .setNegativeButton("取消", com.changanford.common.R.color.color_99) {

                        }.show()

                } else {
                    JumpUtils.instans?.jump(item.jumpDto.jumpCode, item.jumpDto.jumpVal)
                }
            }
            it.butongguo.isVisible = !item.reason.isNullOrEmpty()
            it.reason.text = item.reason ?: ""
            it.reedit.setOnClickListener {
            }
            it.reedit.isVisible = item.showReedit()
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
        val tvHomeSignUpTime = holder.getView<AppCompatTextView>(R.id.tv_home_sign_up_time)
        GlideUtils.loadBD(item.wonderfulPic, ivActs)
        tvTips.text = item.title
        tvHomeActTimes.text = item.getActTimeS()

        btnState.text = item.getTimeStateStr()
        if (item.wonderfulType != 2) {// 不是问卷活动
            if (item.jumpType.toInt() == 3) { // 是常规活动 及报名活动
                tvHomeSignUpTime.visibility = View.VISIBLE
                tvHomeSignUpTime.text = item.getSignTimes()
            } else {
                tvHomeSignUpTime.visibility = View.GONE
            }
        }
        when (item.wonderfulType) {
            0 -> {
                tvTagTwo.text = "线上活动"
                tvHomeActAddress.visibility = View.GONE
            }
            1 -> {
                tvTagTwo.text = "线下活动"
                if (TextUtils.isEmpty(item.city)) {
                    tvHomeActAddress.visibility = View.GONE
                } else {
                    tvHomeActAddress.visibility = View.VISIBLE
                    tvHomeActAddress.text = item.city
                }


            }
            2 -> {
                tvTagTwo.text = "调查问卷"
                tvHomeActTimes.text = item.getEndTimeTips()
                tvHomeActAddress.visibility = View.GONE
            }
            3 -> {
                tvTagTwo.text = "福域活动"
                tvHomeActAddress.visibility = View.GONE
            }
        }
        when (item.official) {
            0 -> {
                tvTagOne.text = context.getString(R.string.platform_acts)
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
        if (TextUtils.isEmpty(item.authors?.getMemberNames())) {
            tvSubtitle.visibility = View.GONE
        } else {
            tvSubtitle.visibility = View.VISIBLE
        }
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
        if (TextUtils.isEmpty(item.getTopic())) {
            tvContent.visibility = View.GONE
        } else {
            tvContent.visibility = View.VISIBLE
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
                                GIOUtils.infoLickClick(
                                    "发现-推荐",
                                    item.artSpecialTopicTitle,
                                    item.artId,
                                    item.artTitle
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
                                GIOUtils.cancelInfoLickClick(
                                    "发现-推荐",
                                    item.artSpecialTopicTitle,
                                    item.artId,
                                    item.artTitle
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
        tvCommentCount.setOnTouchListener { v, event ->
            when (item.rtype) {
                1 -> {//资讯
                    GIOUtils.clickCommentInfo(
                        "发现-推荐",
                        item.artSpecialTopicTitle,
                        item.artId,
                        item.artTitle
                    )
                }
                2 -> {//帖子
                    GIOUtils.clickCommentPost(
                        "发现-推荐",
                        item.postsTopicId,
                        item.postsTopicName,
                        item.authors?.authorId,
                        item.postsId,
                        item.title,
                        item.postsCircleId,
                        item.postsCircleName
                    )
                }
            }
            false
        }
        tvTimeAndViewCount.text = item.getTimeAdnViewCount()
        val tvTopic = holder.getView<TextView>(R.id.tv_topic)
        if (TextUtils.isEmpty(item.getContent()) || item.rtype == 2) {
            tvTopic.text = ""
            tvTopic.visibility = View.GONE
        } else {
            tvTopic.visibility = View.VISIBLE
            tvTopic.text = item.getContent()
        }
        item.authors?.let {
            setFollowState(btnFollow, it)
        }

        if (item.authors?.authorId == MConstant.userId) {
            btnFollow.visibility = View.GONE
        } else {
            btnFollow.visibility = View.VISIBLE
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
            1 -> {// 资讯
                tvNewsTag.visibility = View.VISIBLE
                if (!TextUtils.isEmpty(item.artVideoTime)) {
                    tvVideoTime.text = item.artVideoTime
                }

                tvVideoTime.visibility = View.VISIBLE
                tvNewsTag.text = "资讯"
                ivPlay.visibility = if (item.isArtVideoType()) View.VISIBLE else View.GONE
                tvVideoTime.visibility = if (item.isArtVideoType()) View.VISIBLE else View.GONE
            }
            2 -> {// 帖子
                tvNewsTag.visibility = View.GONE
                if (!TextUtils.isEmpty(item.postsVideoTime)) {
                    tvVideoTime.text = item.postsVideoTime
                }
                tvVideoTime.visibility = View.VISIBLE
                ivPlay.visibility = if (item.postsType == 3) View.VISIBLE else View.GONE
                tvVideoTime.visibility = if (item.postsType == 3) View.VISIBLE else View.GONE
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
        getFollow(authorBaseVo.authorId, followType)
        if (followType == 1) {
            // 埋点 关注
            BuriedUtil.instant?.discoverFollow(authorBaseVo.nickname)
            GIOUtils.followClick(authorBaseVo.authorId, authorBaseVo.nickname, "发现-推荐")
        } else {
            GIOUtils.cancelFollowClick(authorBaseVo.authorId, authorBaseVo.nickname, "发现-推荐")
        }

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

    // 资讯点赞
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
                            GIOUtils.postLickClick(
                                "发现-推荐",
                                item.postsTopicId,
                                item.postsTopicName,
                                item.authors?.authorId,
                                item.postsId,
                                item.title,
                                item.postsCircleId,
                                item.postsCircleName
                            )
//                            "点赞成功".toast()
                            item.isLike = 1
                            tvLikeView.setThumb(R.mipmap.home_comment_like, true)
                            item.postsLikesCount++
                        } else {
                            GIOUtils.cancelPostLickClick(
                                "发现-推荐",
                                item.postsTopicId,
                                item.postsTopicName,
                                item.authors?.authorId,
                                item.postsId,
                                item.title,
                                item.postsCircleId,
                                item.postsCircleName
                            )
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
