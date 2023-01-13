package com.changanford.my.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.MyApp
import com.changanford.common.bean.MessageBean
import com.changanford.common.bean.MessageItemData
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.ConfirmPop
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.*
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import me.leolin.shortcutbadger.ShortcutBadger
import java.util.zip.Inflater

/**
 *  文件名：MineMessageUI
 *  创建者: zcy
 *  创建日期：2020/5/22 19:30
 *  描述: 消息列表
 *  修改描述：包括互动和交易入口，全部系统消息。title的数字包含所有消息未读，点击全部已读，所有消息全部已读（包含互动和交易里的）
 */
@Route(path = ARouterMyPath.MineMessageUI)
class MineMessageUI : BaseMineUI<RefreshLayoutWithTitleBinding, SignViewModel>() {

    var adapter = MessageAdapter()
    lateinit var adapter2: MessageAdapter2

    var times = 0//记录全部已读次数，3才能满足条件
    var total = 0
    override fun initView() {

        binding.mineToolbar.toolbarTitle.text = "消息"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }
        setSaveText(1)
        binding.mineToolbar.toolbarSave.setOnClickListener {
            var pop = ConfirmPop(this)
            pop.contentText.text = "确认全部标记为已读？"
            pop.submitBtn.setOnClickListener {
                pop.dismiss()
                viewModel.changeAllToRead(1)
                viewModel.changeAllToRead(2)
                viewModel.changeAllToRead(3)
            }
            pop.showPopupWindow()
        }
        viewModel.changeAllToRead.observe(this, Observer {
            if (it) {
                times++
                if (times >= 3) {
                    toastShow("消息已读标记成功")
                    ShortcutBadger.applyCount(this, 0)
                    setSaveText(0)
                    initData()
                }
            }
        })
        adapter2 = MessageAdapter2(this, this, {
            viewModel.changAllMessage("${adapter2?.data?.get(it)?.userMessageId}")
            adapter2?.data?.get(it)?.status = 1
            adapter2?.notifyItemChanged(it + 1)
        }) { id, pos ->
            viewModel.delUserMessage(id) {
                it.onSuccess {
                    adapter?.data?.removeAt(pos)
                    adapter?.notifyItemRemoved(pos)
                    adapter?.notifyItemRangeChanged(0, adapter?.itemCount ?: 0)
                }.onWithMsgFailure {
                    it?.toast()
                }
            }
        }
        var headerView = DataBindingUtil.inflate<ActivityMinemessageuiHeaderBinding>(
            LayoutInflater.from(this),
            R.layout.activity_minemessageui_header,
            null,
            false
        )
        headerView.refreshRv.layoutManager = LinearLayoutManager(this)
        headerView.refreshRv.adapter = adapter

        binding.mineRefresh.refreshRv.layoutManager = LinearLayoutManager(this)
        binding.mineRefresh.refreshRv.adapter = adapter2
        adapter2.addHeaderView(headerView.root)

    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.mineRefresh.refreshLayout
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
    }


    override fun initData() {
        super.initData()
        viewModel.queryMessageStatus() {
            it.onSuccess { it ->
                it?.let { data ->
                    total =
                        data.unReadSystemMessageNum + data.unReadHudongNum + data.unReadTradeNum
                    if (total != 0) {
                        binding.mineToolbar.toolbarTitle.text =
                            "消息(${if (total > 99) "99+" else total})"
                        setSaveText(1)
                    } else {
                        binding.mineToolbar.toolbarTitle.text = "消息"
                        setSaveText(0)
                    }

                    var list =
                        arrayListOf(
//                        MessageBean(
//                            R.mipmap.icon_msg_sys,
//                            "系统消息",
//                            "其余系统消息通知",
//                            data.systemMessageStatus
//                        ),
                            MessageBean(
                                R.mipmap.icon_msg_hd,
                                "互动消息",
                                "发现页面站内消息通知",
                                data.hudongStatus,
                                data.unReadHudongNum
                            ),
                            MessageBean(
                                R.mipmap.icon_msg_deal,
                                "交易消息",
                                "三大上门、商城订单消息",
                                data.tradeStatus,
                                data.unReadTradeNum
                            )
                        )
                    adapter.data.clear()
                    adapter.addData(list)
                }
            }
            it.onFailure {
                var list =
                    arrayListOf(
//                        MessageBean(
//                            R.mipmap.icon_msg_sys,
//                            "系统消息",
//                            "其余系统消息通知"
//                        ),
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
//                if (item.messageStatus == 1) {
//                    it.messageStatus.visibility = View.VISIBLE
//                } else {
//                    it.messageStatus.visibility = View.GONE
//                }
                if (item.messageNum != 0) {
                    it.num.isVisible = true
                    it.num.text = "${if (item.messageNum > 99) "99+" else item.messageNum}"
                } else {
                    it.num.isVisible = false

                }
            }

            holder.itemView.setOnClickListener {
                var bundle = Bundle()
                bundle.putInt("messageStatus", item.messageStatus)
                when (holder.adapterPosition + 1) {
                    0 -> {
                        startARouter(ARouterMyPath.MineMessageSysInfoUI, bundle)
                    }
                    1, 2 -> {
                        bundle.putInt("value", holder.adapterPosition + 2)
                        startARouter(ARouterMyPath.MineMessageInfoUI, bundle)
                    }
                }
            }
        }
    }

    //add 2022-09-14
    /**
     * 设置已读按钮状态
     */
    fun setSaveText(status: Int) {
        when (status) {
            1 -> {//有未读
                binding.mineToolbar.toolbarSave.text = "全部已读"
            }
            0 -> {//其他
                binding.mineToolbar.toolbarSave.isEnabled = false
                binding.mineToolbar.toolbarSave.text = "全部已读"
                binding.mineToolbar.toolbarSave.setTextColor(Color.parseColor("#999999"))
            }
        }
    }


    //消息类型 1 系统消息， 2 互动消息，3 交易消息
    override fun initRefreshData(pageNo: Int) {
        super.initRefreshData(pageNo)
        initData()
        viewModel.queryMessageList(
            pageNo,
            1
        ) {
            it.onSuccess { data ->
                data?.let {
                    var list = data.dataList?.let { it ->
                        if (refreshAndLoadMore(
                                data.total,
                                it.size,
                                adapter2!!
                            )
                        ) {
                            var message: StringBuffer = StringBuffer()
                            it.filter { itr -> itr.jumpDataType == 0 || itr.jumpDataType == 99 }
                                .forEach {
                                    message.append("${it.userMessageId},")
                                }
                            viewModel.changAllMessage(message.toString())
                            if (pageNo == 1) {
                                adapter2?.setList(it)
                            } else {
                                adapter2?.addData(it)
                            }
//                            setSaveText(messageStatus)
                        }
                    }
                    if (null == list) {
                        showEmptyView()?.let {
                            adapter2?.setEmptyView(it)
                        }
                    }
                }

            }
            it.onFailure {
                showErrorView(getString(R.string.error_msg))?.let {
                    adapter2?.setEmptyView(it)
                }
            }
        }


    }

    class MessageAdapter2(
        var mContext: Context,
        var lifecycleOwner: LifecycleOwner,
        var read: (Int) -> Unit,
        var func: (String, Int) -> Unit
    ) :
        BaseQuickAdapter<MessageItemData, BaseDataBindingHolder<ItemMineMessageInfoSysBinding>>(
            R.layout.item_mine_message_info_sys
        ) {
        override fun convert(
            holder: BaseDataBindingHolder<ItemMineMessageInfoSysBinding>,
            item: MessageItemData
        ) {

            holder.dataBinding?.let {
                it.title.text = item.messageTitle
                it.date.text = "${TimeUtils.InputTimetamp(item.sendTime.toString())}"
                it.messageDes.text = item.messageContent
                if (item.jumpDataType == 99) {
                    it.arrowR.visibility = View.GONE
                } else {
                    it.arrowR.visibility = View.VISIBLE
                }
                it.messageStatus.isVisible = item.status == 0
                it.delete.setOnClickListener { v ->
                    AlertThreeFilletDialog(mContext).builder().setMsg("是否确认删除本条消息？")
                        .setNegativeButton(
                            "取消", R.color.color_7174
                        ) { v ->
                            it.swipeLayout.quickClose()
                        }
                        .setPositiveButton("确认", R.color.black) {
                            func("${item.userMessageId}", getItemPosition(item))
                        }.show()
                }
                it.item.setOnClickListener {
                    //
                    if (item.jumpDataType == 122) { // 优惠券弹窗
                        lifecycleOwner.launchWithCatch {
                            val body = MyApp.mContext.createHashMap()
                            body["popup"] = "NO"
                            val rKey = getRandomKey()
                            ApiClient.createApi<NetWorkApi>()
                                .receiveList(body.header(rKey), body.body(rKey))
                                .onSuccess { list ->
                                    if (list != null && list.size > 0) {
                                        JumpUtils.instans?.jump(
                                            item.jumpDataType,
                                            item.jumpDataValue
                                        )
                                    } else {
                                        JumpUtils.instans?.jump(118)
                                    }

                                }
                                .onWithMsgFailure {
                                    JumpUtils.instans?.jump(118)
                                }

                        }

                    } else if (item.jumpDataType == 0 || item.jumpDataType == 99) {
                        AlertDialog(context).builder()
                            .setTitle("APP系统升级维护通知")
                            .setMsg("亲爱的用户，平台将于7月25日进行系统维护升级，维护期间，平台将暂时无法访问，给您带来不便，敬请谅解！")
                            .setMsgSize(12)
                            .setMsgColor(ContextCompat.getColor(context, R.color.color_66))
                            .setNegativeButton("我知道了", R.color.pop_1B3B89) { }.show()
                        read(getItemPosition(item))
                    } else {
                        JumpUtils.instans?.jump(
                            item.jumpDataType,
                            item.jumpDataValue
                        )
                    }

                    if (item.jumpDataType != 0 && item.jumpDataType != 99 && item.status == 0) {
                        read(getItemPosition(item))
                    }

                }
            }
        }
    }
}