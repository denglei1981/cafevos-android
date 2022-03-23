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
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsBinding>, item: GoodsItemBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            //维保商品数据需要转换处理
            item.mallWbGoodsId?.apply {
                if("0"!=this)item.maintenanceToGoods()
            }
            dataBinding.model=item
            dataBinding.executePendingBindings()
            dataBinding.imgGoodsCover.load(item.getImgPath())
            dataBinding.tvOrIntegral.visibility=if(item.getLineFbEmpty()) View.GONE else View.VISIBLE
            dataBinding.inVip.model=item
            dataBinding.tvIntegral.visibility=View.VISIBLE
            setTagType(item,dataBinding)
        }
    }
    private fun setTagType(item :GoodsItemBean,dataBinding:ItemGoodsBinding){
        val tagType=item.spuPageTagType
        dataBinding.inVip.lLayoutVip.visibility=View.GONE
        dataBinding.tvTagType.apply {
            visibility=View.VISIBLE
            setBackgroundResource(R.drawable.shadow_b300095b_2dp)
            text=when(tagType){
                "NEW_PRODUCTS"->{
                    setBackgroundResource(R.drawable.shadow_b305bad5_2dp)
                    "新品"
                }
                "HOT_SALE"->{
                    setBackgroundResource(R.drawable.shadow_b30459e6_2dp)
                    "热销"
                }
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
                    setBackgroundResource(R.drawable.shadow_66fa863e_2dp)
                    "秒杀"
                }
                else ->{// MAINTENANCE 维保商品
                    visibility=View.GONE
                    ""
                }
            }

        }
    }
}