package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CommentItem
import com.changanford.common.utilext.load
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsEvaluateBinding
import java.text.SimpleDateFormat


class GoodsEvalutaeAdapter: BaseQuickAdapter<CommentItem, BaseDataBindingHolder<ItemGoodsEvaluateBinding>>(R.layout.item_goods_evaluate){
    private val anonymousUsers by lazy { context.getString(R.string.str_anonymousUsers) }
    @SuppressLint("SimpleDateFormat")
    private val sfDate = SimpleDateFormat("yyyy.MM.dd")
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsEvaluateBinding>, item: CommentItem) {
        holder.dataBinding?.let {
            item.apply {
                evalTimeTxt=sfDate.format(evalTime?:0)
                nickName=if("YES"!=anonymous)nickName else anonymousUsers
                it.imgAvatar.load(avater,R.mipmap.head_default)
                it.model=item
                it.executePendingBindings()
            }
        }
    }
}