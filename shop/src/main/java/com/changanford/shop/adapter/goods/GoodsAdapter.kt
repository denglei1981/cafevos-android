package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.utilext.load
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsBinding


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
            val spuImg=item.spuImgs
            val imgPath=if(spuImg.contains(","))spuImg.split(",")[0] else spuImg
            dataBinding.imgGoodsCover.load(imgPath)
            dataBinding.tvOrIntegral.visibility=if(item.lineFb==null) View.GONE else View.VISIBLE
            dataBinding.inVip.model=item
            dataBinding.tvIntegral.visibility=View.VISIBLE
            setTagType(item,dataBinding)
        }
    }
    private fun setTagType(item :GoodsItemBean,dataBinding:ItemGoodsBinding){
        val tagType=item.spuPageTagType
        dataBinding.inVip.lLayoutVip.visibility=View.GONE
        dataBinding.tvTagType.visibility=View.VISIBLE
        dataBinding.tvTagType.setBackgroundResource(R.drawable.shadow_9900095b_2dp)
        dataBinding.tvTagType.text=when(tagType){
            "NEW_PRODUCTS"->"新品"
            "HOT_SALE"->"热销"
            "MEMBER_DISCOUNT"->{
                dataBinding.inVip.lLayoutVip.visibility=View.VISIBLE
                dataBinding.inVip.tvVipTypeName.setText(R.string.str_vipDiscount)
                dataBinding.tvIntegral.visibility=View.GONE
                "会员折扣"
            }
            "MEMBER_EXCLUSIVE"->{
                dataBinding.inVip.lLayoutVip.visibility=View.VISIBLE
                dataBinding.tvIntegral.visibility=View.GONE
                val secondarySpuPageTagType=item.secondarySpuPageTagType
                dataBinding.inVip.tvVipTypeName.setText(if("MEMBER_DISCOUNT"==secondarySpuPageTagType)R.string.str_vipDiscount else R.string.str_vipExclusive)
                "会员专享"
            }
            "SECKILL"->{
                dataBinding.tvTagType.setBackgroundResource(R.drawable.shadow_66fa863e_2dp)
                "秒杀"
            }
            else ->{
                dataBinding.tvTagType.visibility=View.GONE
                ""
            }
        }
    }
}