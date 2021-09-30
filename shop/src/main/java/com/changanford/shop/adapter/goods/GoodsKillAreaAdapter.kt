package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsKillAreaBinding
import com.changanford.shop.listener.OnPerformListener
import com.changanford.shop.popupwindow.SetNoticPop
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
            dataBinding.btnStates.setStates(getKillStates(item))
            dataBinding.btnStates.setOnClickListener {
                clickBtn(dataBinding,item)
            }
        }
    }
    /**
     * 计算库存比例
    * */
    private fun getStockProportion(sales:Int,stockNow:Int):String{
        return "${sales/stockNow*100}"
    }
    /**
     * isSettedNotice 是否已提醒,可用值:YesNoCodeEnum.NO,YesNoCodeEnum.YES
     * timeState 秒杀状态,可用值:TimeStateEnum.NOT_BEGIN(code=NOT_BEGIN, dbCode=0, message=未开始),
     *                          TimeStateEnum.ON_GOING(code=ON_GOING, dbCode=1, message=进行中),
     *                          TimeStateEnum.ENDED(code=ENDED, dbCode=2, message=已结束)
    * */
    //按钮状态 0 去抢购、 1 已抢光、 2 已结束、3 提醒我、4 取消提醒
    private fun getKillStates(item: GoodsItemBean):Int{
        var killStates=2//默认已结束
        val timeState=item.timeState
        //库存
        val stockNow=item.stockNow
        if("TimeStateEnum.ON_GOING"==timeState){
            killStates=if(stockNow<1)1 else 0
        }else if("TimeStateEnum.NOT_BEGIN"==timeState){
            killStates=if(item.isSettedNotice=="YesNoCodeEnum.NO")3 else 4
        }
        item.killStates=killStates
        return killStates
    }
    private fun clickBtn(dataBinding:ItemGoodsKillAreaBinding,item: GoodsItemBean){
        when(dataBinding.btnStates.getStates()){
            //去抢购
            0-> JumpUtils.instans?.jump(3,item.spuId)
            //设置提醒
            3->{
                if(!WCommonUtil.isNotificationEnabled(context))SetNoticPop(context).showPopupWindow()
                else viewModel.setKillNotices("SET",item.mallMallSpuSeckillRangeId,object :OnPerformListener{
                    override fun onFinish(code: Int) {
                        if(0==code){
                            item.isSettedNotice="YesNoCodeEnum.YES"
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
                        item.isSettedNotice="YesNoCodeEnum.NO"
                        dataBinding.btnStates.setStates(getKillStates(item))
                        ToastUtils.showLongToast(context.getString(R.string.prompt_cancel_setNotic),context)
                    }
                }
            })
        }
    }
}