package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderSkuItem
import com.changanford.common.utilext.load
import com.changanford.common.wutil.ScreenUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemOrderGoodsImgsBinding


class OrderGoodsImgAdapter: BaseQuickAdapter<OrderSkuItem, BaseDataBindingHolder<ItemOrderGoodsImgsBinding>>(R.layout.item_order_goods_imgs){
    private val imgWidth by lazy { (ScreenUtils.getScreenWidth(context)-ScreenUtils.dp2px(context,105f))/3 }
    private val dp20 by lazy { ScreenUtils.dp2px(context,20f) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrderGoodsImgsBinding>, item: OrderSkuItem) {
        holder.dataBinding?.apply {
            val position=holder.absoluteAdapterPosition
//            ScreenUtils.setMargin(layoutRoot,l=if(0==position)20 else 0)
            layoutRoot.setPadding(if(0==position)dp20 else 0,0,if(data.size>3&&position==data.size-1)0 else 0,0)
            val orderType=item.orderType
            val params = imgGoodsCover.layoutParams
            params.width=imgWidth
            params.height=imgWidth
            imgGoodsCover.layoutParams=params
            imgGoodsCover.scaleType= if(orderType>2||0==orderType) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.CENTER_INSIDE
            imgGoodsCover.load(item.skuImg)
            tvOrderType.apply {
                visibility = when(item.busSource?:item.busSourse) {
                    "1","SECKILL" -> {//秒杀
                        setText(R.string.str_seckill)
                        View.VISIBLE
                    }
                    "2","HAGGLE" -> {//砍价
                        setText(R.string.str_bargaining)
                        View.VISIBLE
                    }
                    "3","WB"->{//维保
                        setText(R.string.str_maintenance)
                        View.VISIBLE
                    }
                    else -> View.GONE
                }
            }
        }
    }
}