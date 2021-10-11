package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsKillAreaBinding
import com.changanford.shop.listener.OnPerformListener
import com.changanford.shop.popupwindow.SetNoticPop
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.viewmodel.GoodsViewModel


class GoodsKillAreaAdapter(val viewModel:GoodsViewModel): BaseQuickAdapter<GoodsItemBean, BaseDataBindingHolder<ItemGoodsKillAreaBinding>>(R.layout.item_goods_kill_area), LoadMoreModule {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsKillAreaBinding>, item: GoodsItemBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            item.stockProportion=getStockProportion(item.salesCount,item.stockNow)
            dataBinding.model=item
            dataBinding.executePendingBindings()
            GlideUtils.loadBD(GlideUtils.handleImgUrl(item.imgUrl),dataBinding.imgCover)
            dataBinding.tvOrIntegral.visibility=if(null!=item.fbOfLine)View.VISIBLE else View.GONE
            dataBinding.btnStates.setStates(getKillStates(item))
            dataBinding.btnStates.setOnClickListener {
                clickBtn(dataBinding,item)
            }
            dataBinding.root.setOnClickListener { GoodsDetailsActivity.start(item.spuId) }
        }
    }
    /**
     * 计算库存比例
    * */
    private fun getStockProportion(sales:Int,stockNow:Int):String{
        return "${sales/stockNow*100}"
    }
    /**
     * isSettedNotice 是否已提醒,可用值:NO,YES
     * timeState 秒杀状态,可用值:NOT_BEGIN(code=NOT_BEGIN, dbCode=0, message=未开始),
     *                          ON_GOING(code=ON_GOING, dbCode=1, message=进行中),
     *                          ENDED(code=ENDED, dbCode=2, message=已结束)
    * */
    //按钮状态 0 去抢购、 1 已抢光、 2 已结束、3 提醒我、4 取消提醒
    private fun getKillStates(item: GoodsItemBean):Int{
        var killStates=2//默认已结束
        val timeState=item.timeState
        //库存
        val stockNow=item.stockNow
        if("ON_GOING"==timeState){
            killStates=if(stockNow<1)1 else 0
        }else if("NOT_BEGIN"==timeState){
            killStates=if(item.isSettedNotice=="NO")3 else 4
        }
        item.killStates=killStates
        return killStates
    }
    private fun clickBtn(dataBinding:ItemGoodsKillAreaBinding,item: GoodsItemBean){
        when(dataBinding.btnStates.getStates()){
            //去抢购
            0-> GoodsDetailsActivity.start(item.spuId)
            //设置提醒
            3->{
                if(!WCommonUtil.isNotificationEnabled(context))SetNoticPop(context).showPopupWindow()
                else viewModel.setKillNotices("SET",item.mallMallSpuSeckillRangeId,object :OnPerformListener{
                    override fun onFinish(code: Int) {
                        if(0==code){
                            item.isSettedNotice="YES"
                            dataBinding.btnStates.setStates(getKillStates(item))
                            ToastUtils.showLongToast(context.getString(R.string.prompt_set_setNotic),context)
                        }
                    }
                })
            }
            //取消提醒
            4->viewModel.setKillNotices("CANCEL",item.mallMallSpuSeckillRangeId,object :OnPerformListener{
                override fun onFinish(code: Int) {
                    if(0==code){
                        item.isSettedNotice="NO"
                        dataBinding.btnStates.setStates(getKillStates(item))
                        ToastUtils.showLongToast(context.getString(R.string.prompt_cancel_setNotic),context)
                    }
                }
            })
        }
    }
}