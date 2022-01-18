package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.load
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsKillAreaBinding
import com.changanford.common.listener.OnPerformListener
import com.changanford.shop.popupwindow.SetNoticPop
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.viewmodel.GoodsViewModel


class GoodsKillAreaAdapter(val viewModel:GoodsViewModel): BaseQuickAdapter<GoodsItemBean, BaseDataBindingHolder<ItemGoodsKillAreaBinding>>(R.layout.item_goods_kill_area), LoadMoreModule {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsKillAreaBinding>, item: GoodsItemBean) {
        holder.dataBinding?.apply {
            btnStates.setStates(getKillStates(item))
            item.stockProportion= WCommonUtil.getPercentage(item.salesCount.toDouble(),item.stockPlusSalesCount.toDouble(),0)
            val imgUrl=item.imgUrl
            val imgPath=if(imgUrl.contains(","))imgUrl.split(",")[0] else imgUrl
            imgCover.load(imgPath)
            val fbOfLine=item.fbOfLine
            tvOrIntegral.visibility=if(null!=fbOfLine&&fbOfLine!="0")View.VISIBLE else View.GONE
            tvStockPlusSalesCount.setText("${item.stockPlusSalesCount}")
            btnStates.setOnClickListener {
                clickBtn(this,item)
            }
            model=item
            executePendingBindings()
        }
    }
    /**
     * isSettedNotice 是否已提醒,可用值:NO,YES
     * timeState 秒杀状态,可用值:NOT_BEGIN(code=NOT_BEGIN, dbCode=0, message=未开始),
     *                          ON_GOING(code=ON_GOING, dbCode=1, message=进行中),
     *                          ENDED(code=ENDED, dbCode=2, message=已结束)
    * */
    //按钮状态 0 去抢购、 1 已抢光、 2 已结束、3 提醒我、4 取消提醒 10已提醒
    private fun getKillStates(item: GoodsItemBean):Int{
        val killStates: Int
        val timeState=item.timeState
        var salesCount=item.salesCount
        //库存
        val stockNow=item.stockNow
        when (timeState) {
            //进行中
            "ON_GOING" -> {
                killStates=if(stockNow<1)1 else 0
            }
            //未开始
            "NOT_BEGIN" -> {
                killStates=if(item.isSettedNotice=="NO")3 else 10
            }
            //已结束
            else -> {
                killStates=2
                //强制 销量=总库存
                salesCount=item.stockPlusSalesCount
            }
        }
        item.salesCount=salesCount
        item.killStates=killStates
        return killStates
    }
    private fun clickBtn(dataBinding:ItemGoodsKillAreaBinding,item: GoodsItemBean){
        when(dataBinding.btnStates.getStates()){
            //去抢购
            0->{
                item.apply {
//                    GoodsDetailsActivity.start(spuId)
                    GoodsDetailsActivity.start(getJdType(),jumpDataValue?:spuId)
                }
            }
            //设置提醒
            3->{
                if(!WCommonUtil.isNotificationEnabled(context))SetNoticPop(context).showPopupWindow()
                else viewModel.setKillNotices("SET",item.mallMallSpuSeckillRangeId,object :
                    OnPerformListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onFinish(code: Int) {
                        if(0==code){
                            item.isSettedNotice="YES"
                            dataBinding.btnStates.setStates(getKillStates(item))
                            ToastUtils.showLongToast(context.getString(R.string.prompt_set_setNotic),context)
                            notifyDataSetChanged()
                        }
                    }
                })
            }
            //取消提醒-暂时不做
            4->viewModel.setKillNotices("CANCEL",item.mallMallSpuSeckillRangeId,object :
                OnPerformListener {
                override fun onFinish(code: Int) {
                    if(0==code){
                        item.isSettedNotice="NO"
                        dataBinding.btnStates.setStates(getKillStates(item))
                        ToastUtils.showLongToast(context.getString(R.string.prompt_cancel_setNotic),context)
                        notifyDataSetChanged()
                    }
                }
            })
        }
    }
}