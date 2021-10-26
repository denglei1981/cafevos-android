package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CommentItem
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsEvaluateBinding
import com.changanford.shop.utils.DateTimeUtil


class GoodsEvalutaeAdapter: BaseQuickAdapter<CommentItem, BaseDataBindingHolder<ItemGoodsEvaluateBinding>>(R.layout.item_goods_evaluate){
    private val anonymousUsers by lazy { context.getString(R.string.str_anonymousUsers) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsEvaluateBinding>, item: CommentItem) {
        holder.dataBinding?.let {
            item.apply {
                evalTimeTxt= DateTimeUtil.formatFriendly(evalTime?:0)
                nickName=if("YES"!=anonymous)nickName else anonymousUsers
                GlideUtils.loadBD(GlideUtils.handleImgUrl(avater),it.imgAvatar)
                it.model=item
                it.executePendingBindings()
            }
        }

    }
}