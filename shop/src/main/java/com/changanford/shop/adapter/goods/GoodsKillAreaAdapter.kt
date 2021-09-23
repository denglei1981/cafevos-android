package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.bean.GoodsBean
import com.changanford.shop.databinding.ItemGoodsKillAreaBinding
import com.changanford.shop.popupwindow.SetNoticPop
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.utils.WCommonUtil


class GoodsKillAreaAdapter: BaseQuickAdapter<GoodsBean, BaseDataBindingHolder<ItemGoodsKillAreaBinding>>(R.layout.item_goods_kill_area), LoadMoreModule {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsKillAreaBinding>, item: GoodsBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
            dataBinding.btnStates.setStates(item.states)
            dataBinding.btnStates.setOnClickListener {
                clickBtn(dataBinding,item)
            }
        }
    }
    private fun clickBtn(dataBinding:ItemGoodsKillAreaBinding,item: GoodsBean){
        when(dataBinding.btnStates.getStates()){
            //去抢购
            0-> GoodsDetailsActivity.start(context,"${item.id}")
            //设置提醒
            3->{
                if(!WCommonUtil.isNotificationEnabled(context))SetNoticPop(context).showPopupWindow()
                else{
                    ToastUtils.showLongToast("执行设置提醒逻辑!",context)
                }
            }
            //取消提醒
            4->ToastUtils.showLongToast("执行取消提醒逻辑!",context)
        }
    }
}