package com.changanford.my.ui

import android.graphics.Color
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CancelVerifyBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.StatusCode
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MConstant.H5_CANCEL_ACCOUNT
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.request.GetRequestResult
import com.changanford.common.util.request.addRecord
import com.changanford.common.util.request.getBizCode
import com.changanford.common.widget.pop.UnregisterVerificationPop
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemClearAccountVerifyBinding
import com.changanford.my.databinding.UiClearAccountConBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

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
    var isChecked: Boolean = false
    private var bizCode = ""

    val clearAdapter: ClearAccountAdapter by lazy {
        ClearAccountAdapter()
    }

    override fun initView() {
        binding.clearToolbar.toolbarTitle.text = "申请注销"

        binding.clearRcy.rcyCommonView.adapter = clearAdapter
        viewModel.clearBean.observe(this, Observer {
            completeRefresh(it, clearAdapter)
            it?.forEach {
                if (it.isFinish != 1) {//如果一个条件不满足，则不能进行下一步
                    isCondition = false
                    return@forEach
                }
            }
            binding.btnClearAccount.isEnabled = isChecked && isCondition
        })

        binding.protocol.setOnClickListener {
            JumpUtils.instans?.jump(1, H5_CANCEL_ACCOUNT)
        }

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            this.isChecked = isChecked
            binding.btnClearAccount.isEnabled = isChecked && isCondition
            if (isChecked) {
                getBizCode(this, MConstant.userAgreementCancellation, object : GetRequestResult {
                    override fun success(data: Any) {
                        bizCode = data.toString()
                    }

                })
            }
        }

        binding.btnClearAccount.setOnClickListener {
            viewModel.getCmcUserStatus()
        }

        //取消注销申请  注销账户成功
        LiveDataBus.get().with(LiveDataBusKey.MINE_CANCEL_ACCOUNT, Boolean::class.java)
            .observe(this
            ) {
                if (it) back()
            }
        viewModel.cmaStateData.observe(this) {
            if (it.code != StatusCode.UNREGISTER_POP) {
                starMineCancelAccount()
            } else {
               showUnregisterVerificationPop(it.msg)
            }
        }
        viewModel.cmcStatePhoneBean.observe(this){
            showUnregisterVerificationPop(it.cancel)
        }
    }

    private fun starMineCancelAccount() {
        if (bizCode.isNotEmpty()) {
            addRecord(bizCode)
        }
        RouterManger.startARouter(ARouterMyPath.MineCancelAccountConfirmUI)
    }

    private fun showUnregisterVerificationPop(msg:String) {
        UnregisterVerificationPop(this,msg) {
            starMineCancelAccount()
        }.apply {
            setBackground(R.color.m_pop_bg)
            showPopupWindow()
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.clearRcy.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        viewModel.verifyCancelAccount()
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