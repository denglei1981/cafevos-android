package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsBinding
import com.changanford.shop.view.TypefaceTextView


class GoodsAdapter: BaseQuickAdapter<GoodsItemBean, BaseDataBindingHolder<ItemGoodsBinding>>(R.layout.item_goods){
    init {
        this.setEmptyView(R.layout.view_empty)
    }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsBinding>, item: GoodsItemBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
            GlideUtils.loadBD(GlideUtils.handleImgUrl(item.spuImgs),dataBinding.imgGoodsCover)
            dataBinding.tvOrIntegral.visibility=if(item.lineFb==null) View.GONE else View.VISIBLE
            setTagTaype(item.spuPageTagType,dataBinding.tvTagType,dataBinding.inVip.lLayoutVip)
        }
    }
    private fun setTagTaype(tagType:String,tvTagType:TypefaceTextView,vipView: View){
        vipView.visibility=View.GONE
        tvTagType.visibility=View.VISIBLE
        tvTagType.text=when(tagType){
            "NEW_PRODUCTS"->"新品"
            "HOT_SALE"->"热销"
            "MEMBER_DISCOUNT"->"会员折扣"
            "MEMBER_EXCLUSIVE"->{
                vipView.visibility=View.VISIBLE
                "会员专享"
            }
            "SECKILL"->"秒杀"
            else ->{
                tvTagType.visibility=View.GONE
                ""
            }
        }
    }
}