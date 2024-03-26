package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CommentListBean
import com.changanford.circle.databinding.ItemPostDetailsCommentBinding
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.widget.CommentLoadMoreView
import com.changanford.common.MyApp
import com.changanford.common.adapter.LabelAdapter
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.util.CommentUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.SetFollowState
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.ImageOptions
import com.changanford.common.util.ext.loadImage
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.launchWithCatch
import com.changanford.common.util.room.UserDatabase
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig

class PostDetailsCommentAdapter(private val mLifecycleOwner: LifecycleOwner,private val commentType:Int) :
    BaseQuickAdapter<CommentListBean, BaseViewHolder>(R.layout.item_post_details_comment),
    LoadMoreModule {

    private val mineUser by lazy { UserDatabase.getUniUserDatabase(MyApp.mContext) }
    var checkPosition = 0

    init {
        loadMoreModule.loadMoreView = CommentLoadMoreView()
        addChildClickViewIds(R.id.tv_child_count)
        LiveDataBus.get().withs<AuthorBaseVo>(LiveDataBusKey.FOLLOW_USER_CHANGE)
            .observe(mLifecycleOwner) {
                for (data in this.data) {
                    if (data.authorBaseVo.authorId == it.authorId) {
                        data.authorBaseVo.isFollow = it.isFollow
                    }
                }
                this.notifyDataSetChanged()
            }
    }

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: CommentListBean) {
        val binding = DataBindingUtil.bind<ItemPostDetailsCommentBinding>(holder.itemView)
        binding?.apply {
            layoutHeader.ivHeader.loadImage(
                item.authorBaseVo.avatar,
                ImageOptions().apply { circleCrop = true })
            layoutHeader.tvAuthorName.text = item.nickname
            if (!item.memberIcon.isNullOrEmpty()) {
                layoutHeader.ivVip.load(item.authorBaseVo.memberIcon)
            }
            binding.layoutHeader.btnFollow.isVisible =
                MConstant.userId != item.authorBaseVo.authorId
            tvTime.text = item.getTimeAndAddress()
            if (item.authorBaseVo.carOwner.isNullOrEmpty()) {
                layoutHeader.tvSubTitle.isVisible = false
            } else {
                layoutHeader.tvSubTitle.isVisible = true
                layoutHeader.tvSubTitle.text = item.authorBaseVo.carOwner
            }
            binding.layoutHeader.btnFollow.setOnClickListener {
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    followAction(item.authorBaseVo)
                }
            }
            setFollowState(binding.layoutHeader.btnFollow, item.authorBaseVo)
            binding.bean = item
            binding.tvLikeCount.text = item.likesCount.toString()
            binding.ivLike.setImageResource(
                if (item.isLike == 1) {
                    tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.color_1700F4))
                    R.mipmap.circle_comment_like
                } else {
                    tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.color_8016))
                    R.mipmap.circle_comment_no_like
                }

            )
            tvContent.text = item.content
            tvContent.post {
                MineUtils.expandText(tvContent, item.content)
            }
            binding.llLike.setOnClickListener {
                mLifecycleOwner.launchWithCatch {
                    val body = MyApp.mContext.createHashMap()
                    body["commentId"] = item.id
                    body["type"] = commentType
                    val rKey = getRandomKey()
                    ApiClient.createApi<CircleNetWork>()
                        .commentLike(body.header(rKey), body.body(rKey)).also {
                            it.msg.toast()
                            if (it.code == 0) {
                                if (item.isLike == 0) {
                                    item.isLike = 1
                                    item.likesCount++
                                } else {
                                    item.likesCount--
                                    item.isLike = 0
                                }
                                binding.tvLikeCount.text =
                                    if (item.likesCount == 0) "" else item.likesCount.toString()
                                binding.ivLike.setImageResource(
                                    if (item.isLike == 1) {
                                        AnimScaleInUtil.animScaleIn(binding.ivLike)
                                        R.mipmap.circle_comment_like
                                    } else R.mipmap.circle_comment_no_like
                                )
                            }
                        }
                }
            }
            if (item.childCount == 0) {
                llChild.isVisible = false
            } else {
                llChild.isVisible = true
                val childAdapter = PostCommentChildAdapter(lifecycleOwner = mLifecycleOwner)
                childAdapter.setNewInstance(item.childVo)
                binding.rvChild.adapter = childAdapter

                binding.tvChildCount.text = "共${item.childCount}条回复"
                childAdapter.setOnItemClickListener { _, _, position ->
                    val commentBean = childAdapter.getItem(position)
                    CommentUtils.showReplyDialog(
                        context,
                        commentBean.bizId,
                        commentBean.groupId,
                        commentBean.id,
                        commentType,
                        commentBean.authorBaseVo.nickname
                    ) {
                        checkPosition = holder.layoutPosition
                        LiveDataBus.get().with(CircleLiveBusKey.ADD_COMMENT_REPLY).postValue("")
                    }

                }
            }
            val labelAdapter = LabelAdapter(16)
            layoutHeader.rvUserTag.adapter = labelAdapter
            labelAdapter.setNewInstance(item.authorBaseVo.imags)

            layoutHeader.ivHeader.setOnClickListener {
                JumpUtils.instans?.jump(35, item.userId)
            }
        }
    }

    private var nickName: String = ""

    // 关注或者取消
    private fun followAction(authorBaseVo: AuthorBaseVo) {
        LiveDataBus.get().with(LiveDataBusKey.LIST_FOLLOW_CHANGE).postValue(true)
        nickName = authorBaseVo.nickname
        var followType = authorBaseVo.isFollow
        followType = if (followType == 1) 2 else 1
        if (followType == 2) { //取消关注
            cancelFollowDialog(authorBaseVo.authorId, followType)
        } else {
            //埋点
            BuriedUtil.instant?.communityFollow(authorBaseVo.nickname)
            getFollow(authorBaseVo.authorId, followType)
        }

    }

    // 关注。
    private fun getFollow(followId: String, type: Int) {
        BaseApplication.curActivity.launchWithCatch {
            val requestBody = HashMap<String, Any>()
            requestBody["followId"] = followId
            requestBody["type"] = type
            val rkey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .userFollowOrCancelFollow(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    if (type == 1) {
                        toastShow("已关注")
                        GIOUtils.followClick(followId, nickName, "社区-广场")
                    } else {
                        GIOUtils.cancelFollowClick(followId, nickName, "社区-广场")
                        toastShow("取消关注")
                    }
                    notifyAtt(followId, type)
                }.onWithMsgFailure {
                    if (it != null) {
                        toastShow(it)
                    }
                }
        }
    }

    private fun cancelFollowDialog(followId: String, type: Int) {
        QuickPopupBuilder.with(context)
            .contentView(R.layout.dialog_cancel_follow)
            .config(
                QuickPopupConfig()
                    .gravity(Gravity.CENTER)
                    .withClick(R.id.btn_comfir, View.OnClickListener {
                        getFollow(followId, type)
                    }, true)
                    .withClick(R.id.btn_cancel, View.OnClickListener {
                    }, true)
            )
            .show()
    }

    //关注
    fun notifyAtt(userId: String, isFollow: Int) {
        for (data in this.data) {
            if (data.authorBaseVo?.authorId == userId) {
                data.authorBaseVo?.isFollow = isFollow
            }
        }
        this.notifyDataSetChanged()
    }

    private fun setFollowState(btnFollow: TextView, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(context)
        authors.let {
            setFollowState.setFollowState(btnFollow, it, true)
        }
    }

    /**
     * item 一级评论
     * checkAuthor 被点击的用户信息
     */
    fun addChildComment(
        arrayList: ArrayList<CommentListBean>,
        position: Int
    ) {
        val item = getItem(position)
        item.childVo = arrayList
        item.childCount++
        notifyItemChanged(position)
    }
}