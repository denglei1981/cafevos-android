package com.changanford.my.ui

import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.DialogBottomBean
import com.changanford.common.bean.GrowUpItem
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.ConfirmTwoBtnPop
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.widget.SelectDialog
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.GrowUpAndJifenViewHolder
import com.changanford.my.databinding.ItemGrowUpBinding
import com.changanford.my.databinding.UiJifenBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：JiFenList
 *  创建者: zcy
 *  创建日期：2021/9/13 17:27
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineIntegralUI)
class JiFenList : BaseMineUI<UiJifenBinding, SignViewModel>() {
    var rulesDesc: String = ""

    val jfAdapter: JifenAdapter by lazy {
        JifenAdapter()
    }

    override fun initView() {
        updateMainGio("福币明细页", "福币明细页")
        binding.toolbarJifen.toolbar.setBackgroundResource(0)
        binding.toolbarJifen.toolbarTitle.text = "福币明细"
        UserManger.getSysUserInfo()?.integral?.let {
            binding.myJifenNum.text = "${it.toInt()}"
        }

        binding.toolbarJifen.toolbarSave.text = "..."
        binding.toolbarJifen.toolbarSave.setTextColor(Color.parseColor("#ffffff"))
        binding.toolbarJifen.toolbarSave.visibility = View.VISIBLE
        binding.toolbarJifen.toolbarSave.textSize = 24f
        binding.toolbarJifen.toolbarSave.setOnClickListener {
            SelectDialog(
                this,
                R.style.transparentFrameWindowStyle,
                MineUtils.listIntegral,
                "",
                1,
                SelectDialog.SelectDialogListener() { view: View, i: Int, dialogBottomBean: DialogBottomBean ->
                    when (i) {
                        0 -> {
                            JumpUtils.instans?.jump(1, MConstant.H5_MINE_FORD_AGREEMENT)
                        }
                        1 -> {
                            JumpUtils.instans?.jump(16)
                        }
                    }
                }
            ).show()
        }


        binding.rcyJifen.rcyCommonView.layoutManager = LinearLayoutManager(this)
        binding.rcyJifen.rcyCommonView.adapter = jfAdapter
        viewModel.jifenBean.observe(this, Observer {
            if (null == it) {
                showEmpty()?.let { empty ->
                    jfAdapter.setEmptyView(empty)
                }
            } else {
                it.extend?.let {
                    binding.multiple.text = "${it.multiple}倍加速"
                    rulesDesc = "${it.rulesDesc}"
                }

                completeRefresh(it.dataList, jfAdapter, it.total)
            }
        })

        binding.multiple.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        ConfirmTwoBtnPop(this).apply {
            btnCancel.visibility = View.GONE
            contentText.text = rulesDesc
            btnConfirm.text = "我知道了"
            btnConfirm.setOnClickListener {
                dismiss()
            }
        }.showPopupWindow()
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyJifen.smartCommonLayout
    }

    override fun isUseFullScreenMode(): Boolean {
        return true
    }

    override fun isUserLightMode(): Boolean {
        return false
    }

    override fun initRefreshData(pageSize: Int) {
        task(pageSize)
    }

    private fun task(pageSize: Int) {
        viewModel.mineGrowUp(pageSize, "1")
    }

    inner class JifenAdapter :
        BaseQuickAdapter<GrowUpItem, BaseDataBindingHolder<ItemGrowUpBinding>>(R.layout.item_grow_up) {
        private var source: String = ""

        override fun convert(holder: BaseDataBindingHolder<ItemGrowUpBinding>, item: GrowUpItem) {
            GrowUpAndJifenViewHolder(holder, item, false, source)
        }

        fun setSource(source: String) {
            this.source = source
        }
    }
}