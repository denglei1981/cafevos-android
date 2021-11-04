package com.changanford.my.ui

import android.graphics.Color
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.FansItemBean
import com.changanford.common.databinding.ItemFansBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MineUtils
import com.changanford.common.utilext.load
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

    val fanAdapter: FansAdapter by lazy {
        FansAdapter()
    }

    override fun initView() {
        intent.extras?.getInt(RouterManger.KEY_TO_ID, 1)?.let {
            type = it
            binding.fansToolbar.toolbarTitle.text = if (type == 1) "粉丝" else "关注"
        }
        intent?.extras?.getString(RouterManger.KEY_TO_OBJ)?.let {
            userId = it
        }
        intent?.extras?.getString("title")?.let {
            binding.fansToolbar.toolbarTitle.text = it
        }

        binding.fansRcy.rcyCommonView.adapter = fanAdapter

        viewModel.cancelTip.observe(this, Observer {
            if ("true" == it) {
                initRefreshData(1)
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
                if (type == 2) {
                    it.layout.isSelected = false
                    if (item.isMutualAttention == 1) {
                        it.itemIcon.setImageResource(0)
                        it.itemText.text = "相互关注"
                    } else {
                        it.itemText.text = "已关注"
                        it.itemIcon.setImageResource(R.mipmap.ic_fans_check)
                    }
                    it.itemText.setTextColor(Color.parseColor("#B0B3B5"))
                } else {
                    when (item.isMutualAttention) {
                        1 -> {
                            it.layout.isSelected = false
                            it.itemIcon.setImageResource(R.mipmap.ic_fans_check)
                            it.itemText.setTextColor(Color.parseColor("#B0B3B5"))
                            it.itemText.text = "已关注"
                        }
                        0 -> {
                            it.layout.isSelected = true
                            it.itemText.text = "关注"
                            it.itemText.setTextColor(Color.parseColor("#01025C"))
                            it.itemIcon.setImageResource(0)
                        }
                    }
                }
                it.layout.setOnClickListener {
                    if (type == 2) {
                        cancel(item.authorId.toString(), "2")
                    } else
                        when (item.isMutualAttention) {
                            1 -> {//取消
                                cancel(item.authorId.toString(), "2")
                            }
                            0 -> {//关注
                                cancel(item.authorId.toString(), "1")
                            }
                        }
                }
                it.itemFansIcon.setOnClickListener {
                    RouterManger.param("value", item.authorId)
                        .startARouter(ARouterMyPath.TaCentreInfoUI)
                }
            }
        }
    }

    // 1 关注 2 取消关注
    fun cancel(followId: String, type: String) {
        if (MineUtils.getBindMobileJumpDataType(true)) {
            return
        }
        if (type == "1") {
            viewModel.cancelFans(followId, type)
        } else {
            QuickPopupBuilder.with(this)
                .contentView(R.layout.pop_two_btn)
                .config(
                    QuickPopupConfig()
                        .gravity(Gravity.CENTER)
                        .withClick(R.id.btn_comfir, View.OnClickListener {
                            viewModel.cancelFans(followId, type)
                        }, true)
                        .withClick(R.id.btn_cancel, View.OnClickListener {

                        }, true)
                )
                .show()
        }
    }

}