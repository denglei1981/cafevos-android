package com.changanford.home.shot.adapter

import android.text.TextUtils
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.SetFollowState

import com.changanford.home.bean.BigShotPostBean
import com.changanford.home.bean.BigShotRecommendBean
import com.changanford.home.databinding.ItemBigShotItemsBinding

import com.google.android.material.button.MaterialButton

class BigShotPostListAdapter :
    BaseQuickAdapter<BigShotPostBean, BaseDataBindingHolder<ItemBigShotItemsBinding>>(R.layout.item_big_shot_items) {


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
                when(item.authorBaseVo?.isFollow){
                    0->{
                        it.layoutHeader.btnFollow.text="关注"
                    }
                    1->{
                        it.layoutHeader.btnFollow.text="已关注"
                    }
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

    /**
     *  设置关注状态。
     * */
    private fun setFollowState(btnFollow: MaterialButton, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(context)
        setFollowState.setFollowState(btnFollow, authors)
    }
}