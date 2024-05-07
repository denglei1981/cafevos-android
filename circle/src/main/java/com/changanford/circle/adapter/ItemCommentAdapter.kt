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
import com.changanford.circle.databinding.ItemItemCommentBinding
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
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.SetFollowState
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.ImageOptions
import com.changanford.common.util.ext.loadImage
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.common.text.setSpecificTextColor
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig

class ItemCommentAdapter(private val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<CommentListBean, BaseViewHolder>(R.layout.item_item_comment),
    LoadMoreModule {

    var commentType = 2

    init {
        loadMoreModule.loadMoreView = CommentLoadMoreView()
    }

    override fun convert(holder: BaseViewHolder, item: CommentListBean) {
        val binding = DataBindingUtil.bind<ItemItemCommentBinding>(holder.itemView)
        binding?.let {
            binding.layoutHeader.ivHeader.loadImage(
                item.avatar,
                ImageOptions().apply { circleCrop = true })
//            binding.bean = item
            binding.run {
                layoutHeader.tvAuthorName.text = item.nickname
                if (!item.authorBaseVo.memberIcon.isNullOrEmpty()) {
                    layoutHeader.ivVip.load(item.authorBaseVo.memberIcon)
                }
                tvTime.text = item.getTimeAndAddress()
                if (item.authorBaseVo.carOwner.isNullOrEmpty()) {
                    layoutHeader.tvSubTitle.isVisible = false
                } else {
                    layoutHeader.tvSubTitle.isVisible = true
                    layoutHeader.tvSubTitle.text = item.authorBaseVo.carOwner
                }

                binding.layoutHeader.btnFollow.isVisible =
                    MConstant.userId != item.authorBaseVo.authorId

                binding.layoutHeader.btnFollow.setOnClickListener {
                    if (!MineUtils.getBindMobileJumpDataType(true)) {
                        followAction(item.authorBaseVo)
                    }
                }
                setFollowState(binding.layoutHeader.btnFollow, item.authorBaseVo)
                val labelAdapter = LabelAdapter(16)
                layoutHeader.rvUserTag.adapter = labelAdapter
                labelAdapter.setNewInstance(item.authorBaseVo.imags)
            }
            binding.tvLikeCount.text = if (item.likesCount == 0) "0" else item.likesCount.toString()
            binding.ivLike.setImageResource(
                if (item.isLike == 1) {
                    R.mipmap.circle_comment_like
                } else R.mipmap.circle_comment_no_like
            )
            binding.llLike.setOnClickListener {
                lifecycleOwner.launchWithCatch {
                    val body = MyApp.mContext.createHashMap()
                    body["commentId"] = item.id
                    body["type"] = commentType
                    val rKey = getRandomKey()
                    ApiClient.createApi<CircleNetWork>()
                        .commentLike(body.header(rKey), body.body(rKey)).also {
                            it.msg.toast()
                            LiveDataBus.get().with(LiveDataBusKey.CHILD_COMMENT_STAR).postValue(1)
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
            binding.layoutHeader.ivHeader.setOnClickListener {
                JumpUtils.instans?.jump(35, item.userId.toString())
            }

            contentSty(binding.tvContent, item)
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
        authorBaseVo.isFollow = followType
        LiveDataBus.get().with(LiveDataBusKey.FOLLOW_USER_CHANGE).postValue(authorBaseVo)
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
                        GIOUtils.followClick(followId, nickName, "全部回复")
                    } else {
                        GIOUtils.cancelFollowClick(followId, nickName, "全部回复")
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

    /**
     * 处理回复文本各种样式
     */
    @SuppressLint("SetTextI18n")
    private fun contentSty(contentTv: TextView?, item: CommentListBean) {
        if (item.parentVo.isNullOrEmpty()) {
            contentTv?.text = item.content
            contentTv?.post {
                MineUtils.expandText(contentTv, item.content)
            }
        } else {
            val parentNickName = item.parentVo[0].nickname
            contentTv?.text = "回复${parentNickName}: ${item.content}"
            contentTv?.setSpecificTextColor(
                ContextCompat.getColor(context, R.color.color_1700F4),
                parentNickName
            )
            contentTv?.post {
                MineUtils.expandText(contentTv, contentTv.text.toString())
            }
        }
    }
}