package com.changanford.home.search.adapter

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.bean.PostBean
import com.changanford.common.bean.PostDataBean
import com.changanford.common.net.*
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.api.HomeNetWork
import com.changanford.home.util.launchWithCatch
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

/***
 *
 * 资讯和帖子用同一个。。。
 */
class SearchPostsResultAdapter(private val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<PostDataBean, BaseViewHolder>(R.layout.item_search_result_post) {
    override fun convert(holder: BaseViewHolder, item: PostDataBean) {
        val ivHeader = holder.getView<ShapeableImageView>(R.id.iv_header)
        val tvAuthorName = holder.getView<TextView>(R.id.tv_author_name)
        val tvSubtitle = holder.getView<TextView>(R.id.tv_sub_title)
        val ivPicBig = holder.getView<ShapeableImageView>(R.id.iv_post)
        val tvTime = holder.getView<TextView>(R.id.tv_time)
        val tvContent = holder.getView<TextView>(R.id.tv_content)

        val rvTag=holder.getView<RecyclerView>(R.id.rv_tag)
        if(item.tags!=null&& item.tags?.size!! >0&&item.type==2){// 帖子
            val searchPostTagAdapter= SearchPostTagAdapter()
            searchPostTagAdapter.setList(item.tags)
            rvTag.adapter =searchPostTagAdapter
            rvTag.visibility= View.VISIBLE
        }else{
            rvTag.visibility= View.GONE
        }

        tvContent.text = item.getContentStr()

        tvTime.text = item.timeStr
        GlideUtils.loadBD(item.authorBaseVo?.avatar, ivHeader)
        GlideUtils.loadBD(item.pics, ivPicBig)
        tvAuthorName.text = item.authorBaseVo?.nickname
        tvSubtitle.text = item.authorBaseVo?.getMemberNames()
        val tvCount = holder.getView<AppCompatTextView>(R.id.tv_count)
        tvCount.text = item.getCommentCountAnViewCount()
        val btnFollow = holder.getView<MaterialButton>(R.id.btn_follow)
        item.authorBaseVo?.let {
            setFollowState(btnFollow, it)
        }
        btnFollow.setOnClickListener {
            item.authorBaseVo?.let { it1 -> followAction(btnFollow, it1) }
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