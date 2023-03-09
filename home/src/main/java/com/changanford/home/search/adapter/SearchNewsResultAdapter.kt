package com.changanford.home.search.adapter

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.net.*
import com.changanford.common.util.MConstant
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.api.HomeNetWork
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

/***
 *
 */
class SearchNewsResultAdapter(private val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<InfoDataBean, BaseViewHolder>(R.layout.item_search_result_post) {
    override fun convert(holder: BaseViewHolder, item: InfoDataBean) {
        val ivHeader = holder.getView<ShapeableImageView>(R.id.iv_header)
        val tvAuthorName = holder.getView<TextView>(R.id.tv_author_name)
        val tvSubtitle = holder.getView<TextView>(R.id.tv_sub_title)
        val ivPicBig = holder.getView<ShapeableImageView>(R.id.iv_post)
        val tvTime = holder.getView<TextView>(R.id.tv_time)
        var tvContent = holder.getView<TextView>(R.id.tv_content)

//        tvContent.text = item.getContentStr()
        tvContent.text = item.title

        tvTime.text = item.timeStr
        GlideUtils.loadBD(item.authors?.avatar, ivHeader)
        GlideUtils.loadBD(item.pics, ivPicBig)
        tvAuthorName.text = item.authors?.nickname
        tvSubtitle.text = item.authors?.getMemberNames()
        val tvCount = holder.getView<AppCompatTextView>(R.id.tv_count)
        tvCount.text = item.getCommentCountAnViewCount()
        val btnFollow = holder.getView<MaterialButton>(R.id.btn_follow)
        if (item.authors?.authorId != MConstant.userId) {
            btnFollow.visibility = View.VISIBLE
        } else {
            btnFollow.visibility = View.INVISIBLE
        }
        item.authors?.let {
            setFollowState(btnFollow, it)
        }
        btnFollow.setOnClickListener {
            item.authors?.let { it1 -> followAction(btnFollow, it1) }
        }

    }

    // 关注或者取消
    private fun followAction(btnFollow: MaterialButton, authorBaseVo: AuthorBaseVo) {
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
        getFollow(authorBaseVo.authorId, followType)
    }

    fun setFollowState(btnFollow: MaterialButton, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(context)
        authors.let {
            setFollowState.setFollowState(btnFollow, it, true)
        }
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
}