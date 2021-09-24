package com.changanford.my.ui

import android.graphics.Color
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CancelVerifyBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant.H5_CANCEL_ACCOUNT
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemClearAccountVerifyBinding
import com.changanford.my.databinding.UiClearAccountConBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.launch

/**
 *  文件名：ClearAccountUI
 *  创建者: zcy
 *  创建日期：2021/9/23 16:52
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineCancelAccountUI)
class ClearAccountUI : BaseMineUI<UiClearAccountConBinding, SignViewModel>() {
    var isCondition = true //是否满足全部条件

    val clearAdapter: ClearAccountAdapter by lazy {
        ClearAccountAdapter()
    }

    override fun initView() {

        binding.clearRcy.rcyCommonView.adapter = clearAdapter

        viewModel.clearBean.observe(this, Observer {
            completeRefresh(it, clearAdapter)
            it?.forEach {
                if (it.isFinish != 1) {//如果一个条件不满足，则不能进行下一步
                    isCondition = false
                    return@forEach
                }
            }
        })

        binding.protocol.setOnClickListener {
            JumpUtils.instans?.jump(1, H5_CANCEL_ACCOUNT)
        }

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            binding.btnClearAccount.isEnabled = isChecked && isCondition
        }

        binding.btnClearAccount.setOnClickListener {
            RouterManger.startARouter(ARouterMyPath.MineCancelAccountConfirmUI)
        }

    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.clearRcy.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        lifecycleScope.launch {
            viewModel.verifyCancelAccount()
        }
    }

    override fun hasRefresh(): Boolean {
        return false
    }

    inner class ClearAccountAdapter :
        BaseQuickAdapter<CancelVerifyBean, BaseDataBindingHolder<ItemClearAccountVerifyBinding>>(R.layout.item_clear_account_verify) {
        override fun convert(
            holder: BaseDataBindingHolder<ItemClearAccountVerifyBinding>,
            item: CancelVerifyBean
        ) {
            holder.dataBinding?.let {
                it.title.text = item.condition
                it.des.text = item.conditionDesc
                it.condition.text = if (item.isFinish == 1) "已满足" else "未满足"
                it.condition.setTextColor(Color.parseColor(if (item.isFinish == 1) "#CBCBD4" else "#FC883B"))
            }
        }
    }
}