package com.changanford.my.ui

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.MessageItemData
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.ConfirmPop
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.request.followOrCancelFollow
import com.changanford.common.utilext.*
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemMineMessageInfoBinding
import com.changanford.my.databinding.ItemMineMessageInteractionBinding
import com.changanford.my.databinding.RefreshLayoutWithTitleBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.xiaomi.push.it
import me.leolin.shortcutbadger.ShortcutBadger

/**
 *  文件名：MineMessageInfoUI
 *  创建者: zcy
 *  创建日期：2020/5/22 19:30
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineMessageInfoUI)
class MineMessageInfoUI : BaseMineUI<RefreshLayoutWithTitleBinding, SignViewModel>() {
    private var adapter: MessageAdapter? = null

    var messageType: Int = 2 //消息类型 1 系统消息， 2 互动消息，3 交易消息
    var messageStatus: Int = 1 // 默认有未读消息


    override fun initView() {
        adapter = MessageAdapter(this, {
            viewModel.changAllMessage("${adapter?.data?.get(it)?.userMessageId}")
            adapter?.data?.get(it)?.status = 1
            adapter?.notifyItemChanged(it)
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
        intent.extras?.let {
            messageType = it.getInt("value", 2)
            messageStatus = it.getInt("messageStatus", 1)
        }

        if (messageType == 2) {
            binding.mineRefresh.refreshRv.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.color_F4
                )
            )
        }

        setSaveText(messageStatus)
        binding.mineToolbar.toolbarTitle.text = if (messageType == 2) "互动消息" else "交易消息"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }

        binding.mineToolbar.toolbarSave.setOnClickListener {
            var pop = ConfirmPop(this)
            pop.contentText.text = "确认全部标记为已读？"
            pop.submitBtn.setOnClickListener {
                pop.dismiss()
                viewModel.changeAllToRead(messageType)
            }
            pop.showPopupWindow()
        }
        viewModel.changeAllToRead.observe(this, Observer {
            if (it) {
                ShortcutBadger.applyCount(this, 0)
                toastShow("消息已读标记成功")
                setSaveText(0)
                initRefreshData(1)
            }
        })

        binding.mineRefresh.refreshRv.layoutManager = LinearLayoutManager(this)
        binding.mineRefresh.refreshRv.adapter = adapter

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
        viewModel.queryMessageList(pageNo, messageType) {
            it.onSuccess { data ->
                data?.let {
                    var list = data.dataList?.let {
                        if (refreshAndLoadMore(
                                data.total,
                                it.size,
                                adapter!!
                            )
                        ) {
                            var message: StringBuffer = StringBuffer()
                            it.filter { itr -> itr.jumpDataType == 0 || itr.jumpDataType == 99 }
                                .forEach {
                                    message.append("${it.userMessageId},")
                                }
                            viewModel.changAllMessage(message.toString())
                            it.forEach { bean ->
                                bean.itemType = if (messageType != 2) 0 else 1
                            }
                            if (pageNo == 1) {
                                adapter?.setList(it)
                            } else {
                                adapter?.addData(it)
                            }
//                            setSaveText(messageStatus)
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

    class MessageAdapter(
        var mContext: Context,
        var read: (Int) -> Unit,
        var func: (String, Int) -> Unit
    ) : BaseMultiItemQuickAdapter<MessageItemData, BaseViewHolder>() {

        init {
            addItemType(0, R.layout.item_mine_message_info)
            addItemType(1, R.layout.item_mine_message_interaction)
        }

        override fun convert(holder: BaseViewHolder, item: MessageItemData) {
            when (item.itemType) {
                0 -> {
                    val dataBinding =
                        DataBindingUtil.bind<ItemMineMessageInfoBinding>(holder.itemView)
                    dataBinding?.let {
                        if (!item.messageTitle.isNullOrEmpty()) {
                            it.name.text = item.messageTitle
                        }
                        it.date.text = "${TimeUtils.InputTimetamp(item.sendTime.toString())}"
                        it.content.text = item.messageContent
                        if (item.jumpDataType == 99) {
                            it.look.visibility = View.GONE
                            it.right.isVisible = false
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
                            JumpUtils.instans?.jump(
                                item.jumpDataType,
                                item.jumpDataValue
                            )
                            if (item.jumpDataType != 0 && item.jumpDataType != 99 && item.status == 0) {
                                read(getItemPosition(item))
                            }
                        }
                    }
                }
                1 -> {
                    val binding =
                        DataBindingUtil.bind<ItemMineMessageInteractionBinding>(holder.itemView)
                    binding?.let {
                        setTopMargin(it.root, 18, holder.absoluteAdapterPosition)
                        it.content.text = item.messageContent
                        it.messageStatus.isVisible = item.status == 0
                        it.date.text = TimeUtils.InputTimetamp(item.sendTime.toString())

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
                        binding.ivRight.setCircular(5)
                        if (item.userAvatar.isNullOrEmpty()) {
                            Glide.with(binding.icon).load(R.mipmap.fordicon).into(binding.icon)
                        } else {
                            GlideUtils.loadRound(item.userAvatar,binding.icon,R.mipmap.head_default_circle)
                        }
                        when (item.messageFollowType) {
                            0 -> {//其他消息
                                binding.ivRight.loadCompress(
                                    item.relationBizUrl,
                                    R.mipmap.ic_def_square_img
                                )
                            }
                            1 -> {//关注消息
                                if (item.followStatus == 0) {//被关注
                                    binding.ivRight.setImageResource(R.mipmap.ic_mine_message_no_follow)
                                } else {//互相关注
                                    binding.ivRight.setImageResource(R.mipmap.ic_mine_message_follow)
                                }
                            }
                        }
                        if (item.messageFollowType == 1 && item.followStatus == 0) {
                            binding.tvFollow.visibility = View.VISIBLE
                        } else {
                            binding.tvFollow.visibility = View.GONE
                        }
                        binding.tvFollow.setOnClickListener {
                            item.createId?.let { it1 ->
                                followOrCancelFollow(context as AppCompatActivity, it1, 1) {
                                    read(getItemPosition(item))
                                    item.followStatus = 1
                                    notifyItemChanged(holder.layoutPosition)
                                }
                            }
                        }
                        binding.icon.setOnClickListener {
                            if (!item.createId.isNullOrEmpty()) {
                                read(getItemPosition(item))
                                JumpUtils.instans?.jump(35, item.createId.toString())
                            }
                        }
                        binding.item.setOnClickListener {
                            JumpUtils.instans?.jump(
                                item.jumpDataType,
                                item.jumpDataValue
                            )
                            if (item.jumpDataType != 0 && item.jumpDataType != 99 && item.status == 0) {
                                read(getItemPosition(item))
                            }
                        }
                    }
                }
            }

        }

        private fun setTopMargin(view: View?, margin: Int, position: Int) {
            view?.let {
                val params = view.layoutParams as ViewGroup.MarginLayoutParams
                if (position == 0) {
                    params.topMargin =
                        margin.toIntPx()
                } else params.topMargin = 0
            }

        }
    }
}