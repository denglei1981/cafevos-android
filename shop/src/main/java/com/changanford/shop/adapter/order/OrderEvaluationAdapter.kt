package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.utilext.load
import com.changanford.shop.R
import com.changanford.shop.bean.PostEvaluationBean
import com.changanford.shop.databinding.ItemPostEvaluationBinding
import com.changanford.shop.utils.WCommonUtil.onTextChanged


class OrderEvaluationAdapter: BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<ItemPostEvaluationBinding>>(R.layout.item_post_evaluation){
    private val postBean:ArrayList<PostEvaluationBean> = arrayListOf()
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemPostEvaluationBinding>, item: OrderItemBean) {
        holder.dataBinding?.apply {
            val position=holder.absoluteAdapterPosition
            model=item
            executePendingBindings()
            imgGoodsCover.load(item.skuImg)
            edtContent.onTextChanged {
                initPostBean(this, item, position)
            }
            initPostBean(this, item, position)
        }
    }
    private fun initPostBean(dataBinding:ItemPostEvaluationBinding,item: OrderItemBean,position:Int){
        dataBinding.apply {

        }
    }
}