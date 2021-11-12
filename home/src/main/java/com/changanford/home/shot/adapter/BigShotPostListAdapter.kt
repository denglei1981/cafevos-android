package com.changanford.home.shot.adapter

import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.net.*
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.adapter.LabelAdapter
import com.changanford.home.api.HomeNetWork
import com.changanford.home.bean.BigShotPostBean
import com.changanford.home.databinding.ItemBigShotItemsBinding
import com.changanford.home.util.LoginUtil
import com.changanford.home.util.launchWithCatch
import com.google.android.material.button.MaterialButton

class BigShotPostListAdapter(private val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<BigShotPostBean, BaseDataBindingHolder<ItemBigShotItemsBinding>>(R.layout.item_big_shot_items) {
    init {
        addChildClickViewIds(R.id.tv_author_name, R.id.iv_header)
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
                if (!TextUtils.isEmpty(item.authorBaseVo?.memberIcon)) {
                    GlideUtils.loadBD(item.authorBaseVo?.memberIcon, it.layoutHeader.ivVip)
                    it.layoutHeader.ivVip.visibility = View.VISIBLE
                } else {
                    it.layoutHeader.ivVip.visibility = View.GONE
                }
                it.layoutHeader.tvAuthorName.text = item.authorBaseVo?.nickname
                it.layoutHeader.tvSubTitle.text = item.authorBaseVo?.getMemberNames()
                setFollowState(it.layoutHeader.btnFollow, item.authorBaseVo!!)
                it.layoutHeader.btnFollow.setOnClickListener {
                    item.authorBaseVo?.let { it1 ->
                        if (LoginUtil.isLongAndBindPhone()) {
                            followAction(it as MaterialButton, it1)
                        }
                    }
                }
            }
            // 内容
            GlideUtils.loadBD(item.pics, it.layoutContent.ivPic)
            it.layoutContent.tvContent.text = item.title
            it.layoutContent.tvTime.text = item.timeStr
            it.layoutCount.tvLikeCount.setPageTitleText(item.getLikeCount())
            it.layoutCount.tvTimeLookCount.setPageTitleText(item.getViewCount())
            it.layoutCount.tvCommentCount.setPageTitleText(item.getCommentCount())
            val rvUserTag = holder.getView<RecyclerView>(R.id.rv_user_tag)
            if (item.authorBaseVo != null) {
                val labelAdapter = LabelAdapter(16)
                rvUserTag.adapter = labelAdapter
                labelAdapter.setNewInstance(item.authorBaseVo?.imags)
            }
            it.layoutCount.tvLikeCount.setOnClickListener { l ->
                if (LoginUtil.isLongAndBindPhone()) {
                    likePost(it, item)
                }
            }
            when (item.type) {
                3 -> {
                    it.layoutContent.ivPlay.visibility = View.VISIBLE
                }
                else -> {
                    it.layoutContent.ivPlay.visibility = View.GONE
                }
            }

        }
    }

    // 关注或者取消
    private fun followAction(btnFollow: MaterialButton, authorBaseVo: AuthorBaseVo) {
        var followType = authorBaseVo.isFollow
        followType = if (followType == 1) 2 else 1
//        authorBaseVo.isFollow = followType
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
                    notifyAtt(followId, type)
                }.onWithMsgFailure {
                    toastShow(it.toString())
                }
        }
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

    private fun likePost(binding: ItemBigShotItemsBinding, item: BigShotPostBean) {
        val activity = BaseApplication.curActivity as AppCompatActivity
        activity.launchWithCatch {
            val body = MyApp.mContext.createHashMap()
            body["postsId"] = item.postsId
            val rKey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .actionPostLike(body.header(rKey), body.body(rKey)).also {
                    if (it.code == 0) {
                        if (item.isLike == 0) {
                            item.isLike = 1
                            binding.layoutCount.tvLikeCount.setThumb(
                                R.mipmap.home_comment_like,
                                true
                            )
                            item.likesCount++
                        } else {
                            item.isLike = 0
                            item.likesCount--
                            binding.layoutCount.tvLikeCount.setThumb(
                                R.drawable.icon_big_shot_unlike,
                                false
                            )
                        }
                        binding.layoutCount.tvLikeCount.setPageTitleText(item.getLikeCount())
                    } else {
                        it.msg.toast()
                    }
                }
        }
    }

}