package com.changanford.home.shot.adapter

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.net.*
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.api.HomeNetWork
import com.changanford.home.bean.BigShotPostBean
import com.changanford.home.databinding.ItemBigShotItemsBinding
import com.changanford.home.util.launchWithCatch
import com.google.android.material.button.MaterialButton

class BigShotPostListAdapter(private val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<BigShotPostBean, BaseDataBindingHolder<ItemBigShotItemsBinding>>(R.layout.item_big_shot_items) {
    init {
        addChildClickViewIds(R.id.tv_author_name,R.id.iv_header)
    }
    override fun convert(
        holder: BaseDataBindingHolder<ItemBigShotItemsBinding>,
        item: BigShotPostBean
    ) {
        holder.dataBinding?.let {
//           it.layoutHeader.ivHeader=item.authorBaseVo.avatar;
            // 作者信息
            if (item.authorBaseVo != null) {// 假数据的作者为空。。
                GlideUtils.loadBD(item.authorBaseVo?.avatar, it.layoutHeader.ivHeader)
                if(!TextUtils.isEmpty(item.authorBaseVo?.memberIcon)){
                    GlideUtils.loadBD(item.authorBaseVo?.memberIcon, it.layoutHeader.ivVip)
                    it.layoutHeader.ivVip.visibility= View.VISIBLE
                }else{
                    it.layoutHeader.ivVip.visibility=View.GONE
                }
                it.layoutHeader.tvAuthorName.text = item.authorBaseVo?.nickname
                it.layoutHeader.tvSubTitle.text = item.authorBaseVo?.getMemberNames()
                setFollowState(it.layoutHeader.btnFollow,item.authorBaseVo!!)
                it.layoutHeader.btnFollow.setOnClickListener {
                    item.authorBaseVo?.let { it1 -> followAction(it as MaterialButton, it1) }
                }
            }
            // 内容
            GlideUtils.loadBD(item.pics, it.layoutContent.ivPic)
            it.layoutContent.tvContent.text = item.content
            it.layoutContent.tvTime.text = item.getTimeShow()
            it.layoutCount.tvLikeCount.setPageTitleText(item.getLikeCount())
            it.layoutCount.tvTimeLookCount.setPageTitleText(item.getViewCount())
            it.layoutCount.tvCommentCount.setPageTitleText(item.getCommentCount())
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