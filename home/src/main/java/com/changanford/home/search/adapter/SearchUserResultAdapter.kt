package com.changanford.home.search.adapter

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.net.*
import com.changanford.common.util.MConstant
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.api.HomeNetWork
import com.changanford.home.databinding.ItemSearchResultUserBinding
import com.changanford.home.util.LoginUtil
import com.google.android.material.button.MaterialButton

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
            it.btnFollow.setOnClickListener {
                // 判断是否登录。
                if (LoginUtil.isLongAndBindPhone()) {
                    followAction(it as MaterialButton, item, holder.adapterPosition)
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
    private fun followAction(btnFollow: MaterialButton, authorBaseVo: AuthorBaseVo, position: Int) {
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
    fun getFollow(followId: String, type: Int) {
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
    fun setFollowState(btnFollow: MaterialButton, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(context)
        authors.let {
            setFollowState.setFollowState(btnFollow, it, true)
        }
    }
}