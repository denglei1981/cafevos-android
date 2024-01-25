package com.changanford.home.search.adapter

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
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
            it.tvAuthorName.text = item.nickname
            setFollowState(it.btnFollow, item)
            it.btnFollow.setOnClickListener {_->
                // 判断是否登录。
                if (LoginUtil.isLongAndBindPhone()) {
                    followAction(it.btnFollow , item, holder.adapterPosition)
                }
            }
            if (item.userId != MConstant.userId) {
                it.btnFollow.visibility = View.VISIBLE
            } else {
                it.btnFollow.visibility = View.GONE
            }

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
        authorBaseVo.isFollow = followType
        setFollowState(btnFollow, authorBaseVo)
        getFollow(authorBaseVo.userId, followType)
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
                }.onWithMsgFailure {
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