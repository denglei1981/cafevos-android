package com.changanford.my.ui

import android.graphics.Color
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.FansItemBean
import com.changanford.common.databinding.ItemFansBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MineUtils
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiFansBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig


/**
 *  文件名：FansUI
 *  创建者: zcy
 *  创建日期：2021/9/23 10:36
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineFansUI)
class FansUI : BaseMineUI<UiFansBinding, SignViewModel>() {
    var type: Int = 1 //1 粉丝，2 关注
    var userId: String = ""
    private var followId: String = ""

    private val fanAdapter: FansAdapter by lazy {
        FansAdapter()
    }

    override fun initView() {
        intent.extras?.getInt(RouterManger.KEY_TO_ID, 1)?.let {
            type = it
            binding.fansToolbar.toolbarTitle.text = if (type == 1) "粉丝" else "关注"
            updateMainGio(
                "${binding.fansToolbar.toolbarTitle.text}页",
                "${binding.fansToolbar.toolbarTitle.text}页"
            )
        }
        intent?.extras?.getString(RouterManger.KEY_TO_OBJ)?.let {
            userId = it
        }
        intent?.extras?.getString("title")?.let {
            binding.fansToolbar.toolbarTitle.text = it
        }

        binding.fansRcy.rcyCommonView.adapter = fanAdapter
        binding.fansToolbar.toolbar.setNavigationOnClickListener { finish() }

        viewModel.cancelTip.observe(this, Observer {
            if ("true" == it) {
                when (type) {
                    2 -> {
                        fanAdapter.data?.forEach {
                            if (it.authorId == followId) {
                                when (it.isMutualAttention) {
                                    1, 0 -> {//互相关注变更为关注
                                        it.isMutualAttention = 100
                                    }

                                    100 -> {
                                        if (it.isEachOther) {
                                            it.isMutualAttention = 1
                                        } else {
                                            it.isMutualAttention = 0
                                        }
                                    }

                                    else -> {
                                        it.isMutualAttention = 100
                                    }
                                }
                            }
                        }
                        fanAdapter.notifyDataSetChanged()
                    }

                    1 -> {
                        initRefreshData(1)

                    }
                }
            } else {
                showToast(it)
            }
        })
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        viewModel.queryFansList(pageSize, type, userId) {
            it?.data?.let {
                completeRefresh(it.dataList, fanAdapter, it.total)
            }
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.fansRcy.smartCommonLayout
    }

    inner class FansAdapter :
        BaseQuickAdapter<FansItemBean, BaseDataBindingHolder<ItemFansBinding>>(
            R.layout.item_fans
        ) {
        override fun convert(holder: BaseDataBindingHolder<ItemFansBinding>, item: FansItemBean) {
            holder.dataBinding?.let {
                it.itemFansName.text = item.nickname
                it.itemFansIcon.load(item.avatar)
                it.ivVip.isVisible = !item.memberIcon.isNullOrEmpty()
                it.ivVip.load(item.memberIcon)
                it.tvSubTitle.isVisible = !item.carOwner.isNullOrEmpty()
                it.tvSubTitle.text = item.carOwner
                if (type == 2) {//关注
                    it.itemText.setTextColor(Color.parseColor("#80a6a6a6"))
                    it.itemText.setBackgroundResource(R.drawable.bg_bord_80a6_23)
                    when (item.isMutualAttention) {
                        1 -> {
                            it.itemText.text = "互相关注"
                            item.isEachOther = true
                        }

                        100 -> {
                            it.itemText.text = "关注"
                        }

                        else -> {
                            it.itemText.text = "已关注"
                        }
                    }
                } else {//粉丝
                    when (item.isMutualAttention) {
                        1 -> {
                            it.itemText.setTextColor(Color.parseColor("#80a6a6a6"))
                            it.itemText.text = "相互关注"
                            it.itemText.setBackgroundResource(R.drawable.bg_bord_80a6_23)
                        }

                        0 -> {
                            it.itemText.text = "关注"
                            it.itemText.setTextColor(Color.parseColor("#1700f4"))
                            it.itemText.setBackgroundResource(R.drawable.bg_bord_1700f4_23)
                        }

                    }
                }
                it.itemText.setOnClickListener {
                    if (type == 2) {
                        when (item.isMutualAttention) {
                            1 -> {//取消关注
                                cancel(item.authorId.toString(), "2", item.nickname)
                            }

                            100 -> {//关注
                                cancel(item.authorId.toString(), "1", item.nickname)
                            }

                            else -> {
                                cancel(item.authorId.toString(), "2", item.nickname)
                            }
                        }

                    } else
                        when (item.isMutualAttention) {
                            1 -> {//取消
                                cancel(item.authorId.toString(), "2", item.nickname)
                            }

                            0 -> {//关注
                                cancel(item.authorId.toString(), "1", item.nickname)
                            }
                        }
                }
                it.itemFansIcon.setOnClickListener {
//                    RouterManger.param("value", item.authorId)
//                        .startARouter(ARouterMyPath.TaCentreInfoUI)
                    JumpUtils.instans?.jump(35, item.authorId.toString())
                }
            }
        }
    }

    // 1 关注 2 取消关注
    fun cancel(followId: String?, typeFollow: String, nickName: String?) {
        if (followId.isNullOrEmpty() || nickName.isNullOrEmpty()) {
            "用户已注销".toast()
            return
        }
        val pageName = if (type == 1) "我的-粉丝页" else "我的-关注页"
        if (MineUtils.getBindMobileJumpDataType(true)) {
            return
        }
        if (typeFollow == "1") {
            viewModel.cancelFans(followId, typeFollow)
            GIOUtils.followClick(followId, nickName, pageName)
        } else {
            QuickPopupBuilder.with(this)
                .contentView(R.layout.pop_two_btn)
                .config(
                    QuickPopupConfig()
                        .gravity(Gravity.CENTER)
                        .withClick(R.id.btn_comfir, View.OnClickListener {
                            this.followId = followId
                            viewModel.cancelFans(followId, typeFollow)
                            GIOUtils.cancelFollowClick(followId, nickName, pageName)
                        }, true)
                        .withClick(R.id.btn_cancel, View.OnClickListener {

                        }, true)
                )
                .show()
        }
    }

}