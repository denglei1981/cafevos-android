package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.utilext.load
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemKillGoodsBinding
import com.changanford.shop.utils.ScreenUtils
import com.changanford.shop.utils.WCommonUtil


class GoodsKillAdapter: BaseQuickAdapter<GoodsItemBean, BaseDataBindingHolder<ItemKillGoodsBinding>>(R.layout.item_kill_goods), LoadMoreModule {
    private val dp15 by lazy { ScreenUtils.dp2px(context,15f) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemKillGoodsBinding>, item: GoodsItemBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            val position=holder.absoluteAdapterPosition
            val status=item.seckillStatus
            item.seckillStatuTxt=getsStatus(status)
            //总库存=销量+当前库存
            val totalStock:Int=(item.seckillStock?:0)+item.sekillCount
            item.seckillStock=totalStock
            //秒杀结束 强制已卖完则 销量=总库存
            item.sekillCount=if("ENDED"==status)totalStock else item.sekillCount
            //当前销量
            val sekillCount=item.sekillCount
            val robbedPercentage= WCommonUtil.getPercentage(sekillCount.toDouble(),totalStock.toDouble(),0)
            item.robbedPercentage=robbedPercentage
            val spuImg=item.spuImgs
            val imgPath=if(spuImg.contains(","))spuImg.split(",")[0] else spuImg
            dataBinding.imgCover.load(imgPath)
            dataBinding.tvOrIntegral.visibility=if(item.getLineFbEmpty()) View.GONE else View.VISIBLE
//            val seckillNumLimit=item.seckillNumLimit?:"0"
//            dataBinding.tvSeckillNumLimit.visibility=if("0"!=seckillNumLimit)View.VISIBLE else View.INVISIBLE
            when (position) {
                0 -> ScreenUtils.setMargin(holder.itemView,dp15,0,0,0)
                data.size-1 -> ScreenUtils.setMargin(holder.itemView,0,0,dp15,0)
                else -> ScreenUtils.setMargin(holder.itemView,0,0,9,0)
            }
            dataBinding.model=item
            dataBinding.executePendingBindings()
        }
    }
    /**
     * 	秒杀状态,可用值:TimeStateEnum.NOT_BEGIN(code=NOT_BEGIN, dbCode=0, message=未开始),
     * 	TimeStateEnum.ON_GOING(code=ON_GOING, dbCode=1, message=进行中),
     * 	TimeStateEnum.ENDED(code=ENDED, dbCode=2, message=已结束)
    * */
    private fun getsStatus(status:String):String{
        return when(status){
            "NOT_BEGIN"->"未开始"
            "ON_GOING"->"进行中"
            else ->"已结束"
        }
    }
}