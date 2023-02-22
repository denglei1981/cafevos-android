package com.changanford.home.news.adapter

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.constant.preLoadNumber
import com.changanford.common.net.*
import com.changanford.common.util.CountUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.adapter.LabelAdapter
import com.changanford.home.api.HomeNetWork
import com.changanford.home.util.LoginUtil
import com.changanford.home.util.launchWithCatch
import com.changanford.home.widget.DrawCenterTextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView


class NewsListAdapter(
    private val lifecycleOwner: LifecycleOwner, private val isSpecialDetail: Boolean = false
) :
    BaseQuickAdapter<InfoDataBean, BaseViewHolder>(R.layout.item_news_items), LoadMoreModule {

    init {
        loadMoreModule.preLoadNumber = preLoadNumber
    }

    var isShowFollow: Boolean = true
    var isShowTag: Boolean = false
    var type = ""

    init {
        addChildClickViewIds(
            R.id.layout_content,
            R.id.iv_header,
            R.id.tv_author_name,
            R.id.tv_sub_title,
            R.id.tv_time_look_count,
            R.id.tv_comment_count,
        )
    }

    override fun convert(holder: BaseViewHolder, item: InfoDataBean) {
        val ivHeader = holder.getView<ShapeableImageView>(R.id.iv_header)
        val tvAuthorName = holder.getView<TextView>(R.id.tv_author_name)
        val tvSubtitle = holder.getView<TextView>(R.id.tv_sub_title)
        val ivPicBig = holder.getView<ShapeableImageView>(R.id.iv_pic)
        val tag = holder.getView<AppCompatTextView>(R.id.tv_news_tag)
//        GlideUtils.loadBD(item.authors?.avatar, ivHeader)
        ivHeader.loadCompress(item.authors?.avatar)
        ivPicBig.loadCompress(item.pics)
        tvAuthorName.text = item.authors?.nickname
        if (TextUtils.isEmpty(item.authors?.getMemberNames())) {
            tvSubtitle.visibility = View.GONE
        } else {
            tvSubtitle.visibility = View.VISIBLE
        }
//        tvSubtitle.text = item.authors?.getMemberNames()
        val tvContent = holder.getView<TextView>(R.id.tv_content)
        val btnFollow = holder.getView<MaterialButton>(R.id.btn_follow)

        if (item.authors?.authorId != MConstant.userId && isShowFollow) {
            btnFollow.visibility = View.VISIBLE
        } else {
            btnFollow.visibility = View.GONE
        }
//        btnFollow.visibility = if (isShowFollow) View.VISIBLE else View.GONE
        tag.visibility = if (isShowTag) View.VISIBLE else View.GONE
        tag.text = "资讯"
        item.authors?.let {
            setFollowState(btnFollow, it)
        }
        tvContent.text = item.title
        val tvLikeCount = holder.getView<DrawCenterTextView>(R.id.tv_like_count)
        val tvCommentCount = holder.getView<TextView>(R.id.tv_comment_count)
        val tvLookCount = holder.getView<TextView>(R.id.tv_time_look_count)
        val tvTime = holder.getView<TextView>(R.id.tv_time)

        val ivPlay = holder.getView<ImageView>(R.id.iv_play)

        val tvVideoTime = holder.getView<AppCompatTextView>(R.id.tv_video_times)
        tvLikeCount.setPageTitleText(item.likesCount.toString())
        setLikeState(tvLikeCount, item, false)
        tvCommentCount.text = item.getCommentCountResult()
        tvCommentCount.setOnTouchListener { v, event ->
            GIOUtils.clickCommentInfo(
                if (type.isNotEmpty()) {
                    type
                } else if (isSpecialDetail) {
                    "专题详情页"
                } else "发现-资讯",
                item.specialTopicTitle,
                item.artId,
                item.title
            )
            false
        }
        tvLookCount.text = item.getTimeAdnViewCount()
        tvTime.text = item.getTimeAdnViewCount()
        val tvTopic = holder.getView<TextView>(R.id.tv_topic)
        if (TextUtils.isEmpty(item.summary)) {
            tvTopic.visibility = View.GONE
            tvTopic.text = ""
        } else {
            tvTopic.visibility = View.VISIBLE
            tvTopic.text = item.summary
        }
        when (item.type) {
            1, 2 -> {
                ivPlay.visibility = View.GONE
                tvVideoTime.visibility = View.GONE
            }
            3 -> {
                ivPlay.visibility = View.VISIBLE
                tvVideoTime.visibility = View.VISIBLE
                tvVideoTime.text = item.videoTime
            }

        }
        val rvUserTag = holder.getView<RecyclerView>(R.id.rv_user_tag)
        if (item.authors != null) {
            val labelAdapter = LabelAdapter(16)
            rvUserTag.adapter = labelAdapter
            labelAdapter.setNewInstance(item.authors?.imags)
        }
        btnFollow.setOnClickListener {
            // 判断是否登录。
            if (LoginUtil.isLongAndBindPhone()) {
                if (item.authors != null) {
                    followAction(btnFollow, item.authors!!, holder.adapterPosition)

                }
            }
        }
        tvLikeCount.setOnClickListener {
            if (LoginUtil.isLongAndBindPhone()) {
                if (item.authors != null) {
                    if (item.isLike == 0) {
                        item.isLike = 1
                        val likesCount = item.likesCount.plus(1)
                        item.likesCount = likesCount
                        tvLikeCount.setPageTitleText(
                            CountUtils.formatNum(
                                likesCount.toString(),
                                false
                            ).toString()
                        )
                        GIOUtils.infoLickClick(
                            if (type.isNotEmpty()) {
                                type
                            } else if (isSpecialDetail) {
                                "专题详情页"
                            } else "发现-资讯",
                            item.specialTopicTitle,
                            item.artId,
                            item.title
                        )
                    } else {
                        item.isLike = 0
                        val likesCount = item.likesCount.minus(1)
                        item.likesCount = likesCount
                        tvLikeCount.setPageTitleText(
                            CountUtils.formatNum(
                                likesCount.toString(),
                                false
                            ).toString()
                        )
                        GIOUtils.cancelInfoLickClick(
                            if (type.isNotEmpty()) {
                                type
                            } else if (isSpecialDetail) {
                                "专题详情页"
                            } else "发现-资讯",
                            item.specialTopicTitle,
                            item.artId,
                            item.title
                        )
                    }
                    actionLike(item.artId)
                    setLikeState(tvLikeCount, item, true)
                }
            }
        }
    }

    // 关注。
    private fun getFollow(followId: String, typeFollow: Int, nickName: String) {
        val pageName = if (type.isNotEmpty()) {
            type
        } else if (isSpecialDetail) {
            "专题详情页"
        } else "发现-资讯"
        lifecycleOwner.launchWithCatch {
            val requestBody = HashMap<String, Any>()
            requestBody["followId"] = followId
            requestBody["type"] = typeFollow
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .followOrCancelUser(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    notifyAtt(followId, typeFollow)
                    if (typeFollow == 1) {
                        GIOUtils.followClick(followId, nickName, pageName)
                    } else {
                        GIOUtils.cancelFollowClick(followId, nickName, pageName)
                    }
                }.onWithMsgFailure {
                    if (it != null) {
                        toastShow(it)
                    }
                }
        }
    }

    //关注
    fun notifyAtt(userId: String, isFollow: Int) {
        for (data in this.data) {
            if (data.userId == userId) {
                data.authors?.isFollow = isFollow
            }
        }
        this.notifyDataSetChanged()
    }

    // 喜欢
    private fun actionLike(artId: String) {
        lifecycleOwner.launchWithCatch {
            val requestBody = HashMap<String, Any>()
            requestBody["artId"] = artId
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .actionLike(requestBody.header(rkey), requestBody.body(rkey)).also {
                    it.msg.toast()
                }

        }
    }

    fun setLikeState(tvLikeView: DrawCenterTextView, item: InfoDataBean, isAnim: Boolean) {
        if (item.isLike == 0) {
            tvLikeView.setThumb(R.drawable.icon_big_shot_unlike, isAnim)
        } else {
            tvLikeView.setThumb(R.mipmap.home_comment_like, isAnim)
        }
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
        LiveDataBus.get().with(LiveDataBusKey.LIST_FOLLOW_CHANGE).postValue(true)
        var followType = authorBaseVo.isFollow
        followType = if (followType == 1) 2 else 1
        getFollow(authorBaseVo.authorId, followType, authorBaseVo.nickname)
        val pageName = if (type.isNotEmpty()) {
            type
        } else if (isSpecialDetail) {
            "专题详情页"
        } else "发现-资讯"
        when (followType) {
            1 -> {
                GIOUtils.followClick(authorBaseVo.authorId, authorBaseVo.nickname, pageName)
            }
            2 -> {
                GIOUtils.cancelFollowClick(
                    authorBaseVo.authorId,
                    authorBaseVo.nickname,
                    pageName
                )
            }
        }
    }
}
