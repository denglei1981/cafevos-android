package com.changanford.home.search.adapter

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.adapter.LabelAdapter
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.FollowUserChangeBean
import com.changanford.common.bean.PostDataBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.util.MConstant
import com.changanford.common.util.SetFollowState
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.launchWithCatch
import com.changanford.common.util.request.actionLickPost
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.common.utilext.setDrawableLeft
import com.changanford.common.utilext.toast
import com.changanford.home.R
import com.changanford.home.api.HomeNetWork
import com.changanford.home.databinding.ItemSearchResultPostBinding

/***
 *
 * 资讯和帖子用同一个。。。
 */
class SearchPostsResultAdapter(private val mLifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<PostDataBean, BaseDataBindingHolder<ItemSearchResultPostBinding>>(R.layout.item_search_result_post) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemSearchResultPostBinding>,
        item: PostDataBean
    ) {
        holder.dataBinding?.apply {
            GlideUtils.loadBD(item.authorBaseVo?.avatar, layoutUserTitle.ivHeader)
            layoutUserTitle.apply {
                tvAuthorName.text = item.authorBaseVo?.nickname
                tvSubTitle.isVisible = !item.authorBaseVo?.getMemberNames().isNullOrEmpty()
                tvSubTitle.text = item.authorBaseVo?.getMemberNames()
                if (item.authorBaseVo?.authorId != MConstant.userId) {
                    btnFollow.visibility = View.VISIBLE
                } else {
                    btnFollow.visibility = View.INVISIBLE
                }
                if (!item.authorBaseVo?.memberIcon.isNullOrEmpty()){
                    ivVip.load(item.authorBaseVo?.memberIcon)
                }
                if (item.authorBaseVo?.imags != null && item.authorBaseVo?.imags?.size!! > 0) {// 帖子
                    val searchPostTagAdapter = LabelAdapter(22)
                    searchPostTagAdapter.setList(item.authorBaseVo?.imags)
                    rvUserTag.adapter = searchPostTagAdapter
                    rvUserTag.visibility = View.VISIBLE
                } else {
                    rvUserTag.visibility = View.GONE
                }
                item.authorBaseVo?.let {
                    setFollowState(btnFollow, it)
                }
                btnFollow.setOnClickListener {
                    item.authorBaseVo?.let { it1 -> followAction(btnFollow, it1) }
                }
            }

            tvContent.text = item.title
            tvTime.text = item.timeStr
            GlideUtils.loadBD(item.pics, ivPost)

            layoutBottom.apply {
                tvViews.text = item.getViewsResult()
                tvGoods.text = item.getGoodsResult()
                tvComments.text = item.getCommentResult()

                if (item.isLike == 1) {
                    tvGoods.setDrawableLeft(R.mipmap.ic_item_sm_good_y)
                } else {
                    tvGoods.setDrawableLeft(R.mipmap.ic_item_sm_good)
                }

                tvGoods.setOnClickListener {
                    actionLickPost(mLifecycleOwner, item.postsId.toString()) {
                        if (item.isLike == 1) {
                            item.isLike = 0
                            item.likesCount--
                        } else {
                            item.isLike = 1
                            item.likesCount++
                        }
                        notifyItemChanged(holder.layoutPosition)
                    }
                }
            }
        }
    }

    // 关注或者取消
    private fun followAction(btnFollow: TextView, authorBaseVo: AuthorBaseVo) {
        var followType = authorBaseVo.isFollow
        when (followType) {
            1 -> {
                followType = 2
            }

            else -> {
                followType = 1
            }
        }
        getFollow(authorBaseVo.authorId, followType){
            authorBaseVo.isFollow = followType
            setFollowState(btnFollow, authorBaseVo)
        }
    }

    fun setFollowState(btnFollow: TextView, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(context)
        authors.let {
            setFollowState.setFollowState(btnFollow, it, true)
        }
    }

    // 关注。
    fun getFollow(followId: String, type: Int, block: () -> Unit) {
        mLifecycleOwner.launchWithCatch {
            val requestBody = HashMap<String, Any>()
            requestBody["followId"] = followId
            requestBody["type"] = type
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .followOrCancelUser(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    if (type == 1) {
                        "已关注".toast()
                    } else {
                        "取消关注".toast()
                    }
                    block.invoke()
                    LiveDataBus.get().with(LiveDataBusKey.FOLLOW_USER_CHANGE).postValue(
                        FollowUserChangeBean(followId, type)
                    )
                }.onWithMsgFailure {
                    it?.toast()
                }
        }
    }
}