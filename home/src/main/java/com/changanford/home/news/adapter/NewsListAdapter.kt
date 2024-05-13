package com.changanford.home.news.adapter

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.adapter.LabelAdapter
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.constant.preLoadNumber
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.util.MConstant
import com.changanford.common.util.MUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.image.ItemCommonPics
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.common.utilext.load
import com.changanford.common.utilext.setDrawableLeft
import com.changanford.common.utilext.toIntPx
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.api.HomeNetWork
import com.changanford.home.databinding.ItemNewsItemsBinding
import com.changanford.home.util.LoginUtil
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
    var isManage = false

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
        val binding = DataBindingUtil.bind<ItemNewsItemsBinding>(holder.itemView)
        binding?.let {
            it.checkbox.isChecked = item.isCheck
            if (isManage) {
                it.checkbox.isVisible = true
                it.clContent.translationX = 36.toIntPx().toFloat()
            } else {
                it.checkbox.isVisible = false
                it.clContent.translationX = 0f
            }
            MUtils.setTopMargin(it.root, 15, holder.layoutPosition)
            ItemCommonPics.setItemCommonPics(binding.layoutContent.layoutPics, item.getPics())
        }
        val ivHeader = holder.getView<ShapeableImageView>(R.id.iv_header)
        val tvAuthorName = holder.getView<TextView>(R.id.tv_author_name)
        val tvSubtitle = holder.getView<TextView>(R.id.tv_sub_title)
        ivHeader.loadCompress(item.authors?.avatar)
        tvAuthorName.text = item.authors?.nickname
        if (TextUtils.isEmpty(item.authors?.getMemberNames())) {
            tvSubtitle.visibility = View.GONE
        } else {
            tvSubtitle.visibility = View.VISIBLE
            tvSubtitle.text = item.authors?.carOwner
        }
        val tvContent = holder.getView<TextView>(R.id.tv_content)
        val btnFollow = holder.getView<TextView>(R.id.btn_follow)

        if (item.authors?.authorId != MConstant.userId && isShowFollow && !isManage) {
            btnFollow.visibility = View.VISIBLE
        } else {
            btnFollow.visibility = View.GONE
        }
        item.authors?.let {
            setFollowState(btnFollow, it)
        }
        tvContent.text = item.title
        val tvTime = holder.getView<TextView>(R.id.tv_post_time)
        val tvLocation = holder.getView<TextView>(R.id.tv_location)
        val viewCount = holder.getView<TextView>(R.id.tv_view_count)
        val comments = holder.getView<TextView>(R.id.tv_comments)
        val tvLikeCount = holder.getView<TextView>(R.id.tv_like_count)
        val ivVip = holder.getView<ImageView>(R.id.iv_vip)
        if (!item.authors?.memberIcon.isNullOrEmpty()) {
            ivVip.load(item.authors?.memberIcon)
        }
        tvLocation.visibility = View.GONE
        viewCount.text = item.getViewsResult()
        comments.text = item.getCommentCountNew()
        tvTime.text = item.timeStr
        val ivPlay = holder.getView<ImageView>(R.id.iv_play)

        val tvVideoTime = holder.getView<AppCompatTextView>(R.id.tv_video_times)

        tvLikeCount.text = item.getGoodsResult()
        if (item.isLike == 1) {
            tvLikeCount.setDrawableLeft(R.mipmap.item_good_count_light_ic)

        } else {
            tvLikeCount.setDrawableLeft(R.mipmap.item_good_count_ic)
        }
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
                if (isManage) {
                    item.isCheck = !item.isCheck
                    binding?.checkbox?.isChecked = item.isCheck
                    checkIsAllCheck()
                } else {
                    if (item.authors != null) {
                        followAction(btnFollow, item.authors!!, holder.adapterPosition)

                    }
                }
            }
        }
        tvLikeCount.setOnClickListener {
            if (LoginUtil.isLongAndBindPhone()) {
                if (isManage) {
                    item.isCheck = !item.isCheck
                    binding?.checkbox?.isChecked = item.isCheck
                    checkIsAllCheck()
                    return@setOnClickListener
                }
                if (item.authors != null) {
                    if (item.isLike == 0) {
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
                    actionLike(item.artId) {
                        if (item.isLike == 0) {
                            item.isLike = 1
                            val likesCount = item.likesCount.plus(1)
                            item.likesCount = likesCount

                        } else {
                            item.isLike = 0
                            val likesCount = item.likesCount.minus(1)
                            item.likesCount = likesCount

                        }
                        setLikeState(tvLikeCount, item, true)
                        tvLikeCount.text = ("${if (item.likesCount > 0) item.likesCount else "0"}")
                    }
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
                    if (typeFollow == 1) {
                        "已关注".toast()
                    } else {
                        "取消关注".toast()
                    }
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
    private fun actionLike(artId: String, block: () -> Unit) {
        lifecycleOwner.launchWithCatch {
            val requestBody = HashMap<String, Any>()
            requestBody["artId"] = artId
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .actionLike(requestBody.header(rkey), requestBody.body(rkey)).also {
                    it.msg.toast()
                }.onSuccess {
                    block.invoke()
                }

        }
    }

    fun setLikeState(tvLikeView: TextView, item: InfoDataBean, isAnim: Boolean) {
        if (item.isLike == 0) {
            tvLikeView.setDrawableLeft(R.mipmap.item_good_count_ic)
        } else {
            tvLikeView.setDrawableLeft(R.mipmap.item_good_count_light_ic)
        }
    }

    /**
     *  设置关注状态。
     * */
    fun setFollowState(btnFollow: TextView, authors: AuthorBaseVo) {
        val setFollowState = com.changanford.common.util.SetFollowState(context)
        authors.let {
            setFollowState.setFollowState(btnFollow, it, true)
        }
    }

    // 关注或者取消
    private fun followAction(btnFollow: TextView, authorBaseVo: AuthorBaseVo, position: Int) {
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

    fun checkIsAllCheck() {
        if (data.isNullOrEmpty()) {
            LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(false)
            return
        }
        data.forEach {
            if (!it.isCheck) {
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(false)
                return
            }
        }
        LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(true)
    }
}
