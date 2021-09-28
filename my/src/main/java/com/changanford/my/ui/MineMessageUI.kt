package com.changanford.my.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MessageBean
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemMineMessageBinding
import com.changanford.my.databinding.RefreshLayoutWithTitleBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 *  文件名：MineMessageUI
 *  创建者: zcy
 *  创建日期：2020/5/22 19:30
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineMessageUI)
class MineMessageUI : BaseMineUI<RefreshLayoutWithTitleBinding, SignViewModel>() {

    var adapter = MessageAdapter()


    override fun initView() {

        binding.mineToolbar.toolbarTitle.text = "消息"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }

        binding.mineRefresh.refreshRv.layoutManager = LinearLayoutManager(this)
        binding.mineRefresh.refreshRv.adapter = adapter

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
    }


    override fun initData() {
        super.initData()
        viewModel.queryMessageStatus() {
            it.onSuccess {it->
                it?.let {data->

                var list =
                    arrayListOf(
                        MessageBean(
                            R.mipmap.icon_msg_sys,
                            "系统消息",
                            "其余系统消息通知",
                            data.systemMessageStatus
                        ),
                        MessageBean(
                            R.mipmap.icon_msg_hd,
                            "互动消息",
                            "发现页面站内消息通知",
                            data.hudongStatus
                        ),
                        MessageBean(
                            R.mipmap.icon_msg_deal,
                            "交易消息",
                            "三大上门、商城订单消息",
                            data.tradeStatus
                        )
                    )
                    adapter.data.clear()
                    adapter.addData(list)
                }
            }
            it.onFailure {
                var list =
                    arrayListOf(
                        MessageBean(
                            R.mipmap.icon_msg_sys,
                            "系统消息",
                            "其余系统消息通知"
                        ),
                        MessageBean(
                            R.mipmap.icon_msg_hd,
                            "互动消息",
                            "发现页面站内消息通知"
                        ),
                        MessageBean(
                            R.mipmap.icon_msg_deal,
                            "交易消息",
                            "三大上门、商城订单消息"
                        )
                    )
                adapter.data.clear()
                adapter.addData(list)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    class MessageAdapter :
        BaseQuickAdapter<MessageBean, BaseDataBindingHolder<ItemMineMessageBinding>>(R.layout.item_mine_message) {
        override fun convert(
            holder: BaseDataBindingHolder<ItemMineMessageBinding>,
            item: MessageBean
        ) {
            holder.dataBinding?.let {
                it.iconSys.setImageResource(item.iconId)
                it.titleSys.text = item.title
                it.desSys.text = item.des
                //是否有消息
                if (item.messageStatus == 1) {
                    it.messageStatus.visibility = View.VISIBLE
                } else {
                    it.messageStatus.visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                var bundle = Bundle()
                bundle.putInt("messageStatus", item.messageStatus)
                when (holder.adapterPosition) {
                    0 -> {
                        startARouter(ARouterMyPath.MineMessageSysInfoUI, bundle)
                    }
                    1, 2 -> {
                        bundle.putInt("value", holder.adapterPosition + 1)
                        startARouter(ARouterMyPath.MineMessageInfoUI, bundle)
                    }
                }
            }
        }
    }
}