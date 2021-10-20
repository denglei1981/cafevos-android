package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemKillGoodsBinding
import com.changanford.shop.utils.ScreenUtils


class GoodsKillAdapter: BaseQuickAdapter<GoodsItemBean, BaseDataBindingHolder<ItemKillGoodsBinding>>(R.layout.item_kill_goods), LoadMoreModule {
    private val dp15 by lazy { ScreenUtils.dp2px(context,15f) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemKillGoodsBinding>, item: GoodsItemBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            val position=holder.absoluteAdapterPosition
            item.seckillStatus=getsSeckillStatus(item.seckillStatus)
            dataBinding.model=item
            dataBinding.executePendingBindings()
            val spuImg=item.spuImgs
            val imgPath=if(spuImg.contains(","))spuImg.split(",")[0] else spuImg
            GlideUtils.loadBD(GlideUtils.handleImgUrl(imgPath),dataBinding.imgCover)
            dataBinding.tvOrIntegral.visibility=if(null!=item.lineFb) View.VISIBLE else View.GONE
//            val seckillNumLimit=item.seckillNumLimit?:"0"
//            dataBinding.tvSeckillNumLimit.visibility=if("0"!=seckillNumLimit)View.VISIBLE else View.INVISIBLE
            when (position) {
                0 -> ScreenUtils.setMargin(holder.itemView,dp15,0,0,0)
                data.size-1 -> ScreenUtils.setMargin(holder.itemView,0,0,dp15,0)
                else -> ScreenUtils.setMargin(holder.itemView,0,0,9,0)
            }
        }
    }
    /**
     * 	秒杀状态,可用值:TimeStateEnum.NOT_BEGIN(code=NOT_BEGIN, dbCode=0, message=未开始),
     * 	TimeStateEnum.ON_GOING(code=ON_GOING, dbCode=1, message=进行中),
     * 	TimeStateEnum.ENDED(code=ENDED, dbCode=2, message=已结束)
    * */
    private fun getsSeckillStatus(seckillStatus:String):String{
        return when(seckillStatus){
            "NOT_BEGIN"->"未开始"
            "ON_GOING"->"进行中"
            else ->"已结束"
        }
    }
}