package com.changanford.my.ui

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MessageItemData
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.ConfirmPop
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemMineMessageInfoSysBinding
import com.changanford.my.databinding.RefreshLayoutWithTitleBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.xiaomi.push.it
import me.leolin.shortcutbadger.ShortcutBadger

/**
 *  文件名：MineMessageSysInfoUI
 *  创建者: zcy
 *  创建日期：2020/5/22 20:03
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineMessageSysInfoUI)
class MineMessageSysInfoUI : BaseMineUI<RefreshLayoutWithTitleBinding, SignViewModel>() {

    var messageStatus: Int = 1 // 默认有未读消息

    private var adapter : MessageAdapter? = null


    override fun initView() {

        adapter = MessageAdapter(this){id,pos->
            viewModel.delUserMessage(id) {
                it.onSuccess {
                    adapter?.data?.removeAt(pos)
                    adapter?.notifyItemRemoved(pos)
                    adapter?.notifyItemRangeChanged(0,adapter?.itemCount?:0)
                }.onWithMsgFailure {
                    it?.toast()
                }
            }
        }
        intent.extras?.let {
            messageStatus = it.getInt("messageStatus", 1)
        }

        binding.mineToolbar.toolbarTitle.text = "系统消息"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }

        binding.mineToolbar.toolbarSave.setOnClickListener {
            var pop = ConfirmPop(this)
            pop.contentText.text = "确认全部标记为已读？"
            pop.submitBtn.setOnClickListener {
                pop.dismiss()
                viewModel.changeAllToRead(1)
            }
            pop.showPopupWindow()
        }
        setSaveText(0)
        viewModel.changeAllToRead.observe(this, Observer {
            if (it) {
                toastShow("消息已读标记成功")
                ShortcutBadger.applyCount(this,0)
                setSaveText(0)
            }
        })

        binding.mineRefresh.refreshRv.layoutManager = LinearLayoutManager(this)
        binding.mineRefresh.refreshRv.adapter = adapter

        adapter?.setOnItemClickListener { ad, view, position ->
//            JumpUtils.instans?.jump(
//                adapter?.getItem(position)?.jumpDataType,
//                adapter?.getItem(position)?.jumpDataValue
//            )
//            if (adapter.getItem(position).status == 0) {
//                viewModel.changMessage(adapter.getItem(position).userMessageId.toString())
//            }
        }
    }

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

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.mineRefresh.refreshLayout
    }

    override fun hasRefresh(): Boolean {
        return true
    }

    //消息类型 1 系统消息， 2 互动消息，3 交易消息
    override fun initRefreshData(pageNo: Int) {
        super.initRefreshData(pageNo)
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
                                adapter!!
                            )
                        ) {
                            var message: StringBuffer = StringBuffer()
                            it.forEach {
                                message.append("${it.userMessageId},")
                            }
                            viewModel.changAllMessage(message.toString())
                            adapter?.addData(it)
                            setSaveText(messageStatus)
                        }
                    }
                    if (null == list) {
                        showEmptyView()?.let {
                            adapter?.setEmptyView(it)
                        }
                    }
                }

            }
            it.onFailure {
                showErrorView(getString(R.string.error_msg))?.let {
                    adapter?.setEmptyView(it)
                }
            }
        }


    }

    class MessageAdapter(var mContext: Context,var func: (String,Int)->Unit) :
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
                it.delete.setOnClickListener {v->
                    AlertThreeFilletDialog(mContext).builder().setMsg("是否确认删除本条消息？")
                        .setNegativeButton(
                            "取消", R.color.color_7174
                        ) { v->
                            it.swipeLayout.quickClose()
                        }
                        .setPositiveButton("确认", R.color.black) {
                            func("${item.userMessageId}",getItemPosition(item))
                        }.show()
                }
                it.item.setOnClickListener {
                    JumpUtils.instans?.jump(
                        item.jumpDataType,
                        item.jumpDataValue
                    )
                }
            }
        }
    }
}