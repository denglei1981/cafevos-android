package com.changanford.my.adapter

import android.text.Html
import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.bean.PostBean
import com.changanford.common.bean.PostDataBean
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemMysJoinCirlceBinding
import com.changanford.evos.databinding.ItemMysLikePostBinding


class MyStarPostAdapter :
    BaseQuickAdapter<PostDataBean, BaseDataBindingHolder<ItemMysLikePostBinding>>(R.layout.item_mys_like_post) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemMysLikePostBinding>,
        item: PostDataBean
    ) {
        holder.dataBinding?.let { t ->
            GlideUtils.loadBD(item.pics, t.ivCircle)
            t.tvCircleTitle.text = item.getShowTitle()
            if(TextUtils.isEmpty(item.getContentStr())){
                t.tvCircleDesc.text = item.getContentStr()
            }else{
                t.tvCircleDesc.text = Html.fromHtml(item.getContentStr())
            }
            t.tvCommentCount.text = CountUtils.formatNum(item.commentCount.toString(), false)
            t.tvLikeCount.setPageTitleText(
                CountUtils.formatNum(item.likesCount.toString(), false).toString()
            )

        }
    }


}