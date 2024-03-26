package com.changanford.home.search.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.adapter.LabelAdapter
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.util.MConstant
import com.changanford.common.util.SetFollowState
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toIntPx
import com.changanford.common.utilext.toast
import com.changanford.home.R
import com.changanford.home.api.HomeNetWork
import com.changanford.home.databinding.ItemSearchResultUserBinding
import com.changanford.home.util.LoginUtil

class SearchUserResultAdapter(val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<AuthorBaseVo, BaseDataBindingHolder<ItemSearchResultUserBinding>>(
        R.layout.item_search_result_user
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemSearchResultUserBinding>,
        item: AuthorBaseVo
    ) {
        //     val headFrameName:String="", 这里取 这些
        //    val headFrameImage:String=""
        holder.dataBinding?.let { it ->
            GlideUtils.loadBD(item.avatar, it.ivHeader)
            if (!item.memberIcon.isNullOrEmpty()) {
                it.ivVip.load(item.memberIcon)
            }
            if (item.imags != null && item.imags?.size!! > 0) {// 帖子
                val searchPostTagAdapter = LabelAdapter(22)
                searchPostTagAdapter.setList(item.imags)
                it.rvUserTag.adapter = searchPostTagAdapter
                it.rvUserTag.visibility = View.VISIBLE
            } else {
                it.rvUserTag.visibility = View.GONE
            }
            it.tvAuthorName.text = item.nickname
            it.tvSubTitle.text = item.getMemberNames()
            it.tvSubTitle.isVisible = !item.getMemberNames().isNullOrEmpty()
            setFollowState(it.btnFollow, item)
            it.btnFollow.setOnClickListener { _ ->
                // 判断是否登录。
                if (LoginUtil.isLongAndBindPhone()) {
                    followAction(it.btnFollow, item, holder.adapterPosition)
                }
            }
            if (item.userId != MConstant.userId) {
                it.btnFollow.visibility = View.VISIBLE
            } else {
                it.btnFollow.visibility = View.GONE
            }
            setTopMargin(it.root, 15, holder.layoutPosition)
        }

    }

    private fun setTopMargin(view: View?, margin: Int, position: Int) {
        view?.let {
            val params = view.layoutParams as ViewGroup.MarginLayoutParams
            if (position == 0) {
                params.topMargin =
                    margin.toIntPx()
            } else params.topMargin = 0
        }

    }

    // 关注或者取消
    private fun followAction(btnFollow: TextView, authorBaseVo: AuthorBaseVo, position: Int) {
        var followType = authorBaseVo.isFollow
        when (followType) {
            1 -> {
                followType = 2
            }

            else -> {
                followType = 1
            }
        }
        getFollow(authorBaseVo.userId, followType) {
            authorBaseVo.isFollow = followType
            setFollowState(btnFollow, authorBaseVo)
        }
    }

    // 关注。
    private fun getFollow(followId: String, type: Int, block: () -> Unit) {
        lifecycleOwner.launchWithCatch {
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
                }.onWithMsgFailure {
                    it?.toast()
                }
        }
    }

    /**
     *  设置关注状态。
     * */
    fun setFollowState(btnFollow: TextView, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(context)
        authors.let {
            setFollowState.setFollowState(btnFollow, it, true)
        }
    }
}