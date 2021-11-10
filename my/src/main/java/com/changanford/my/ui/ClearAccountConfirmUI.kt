package com.changanford.my.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CancelReasonBeanItem
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemClearAccountReasonBinding
import com.changanford.my.databinding.UiClearAccountConfirmBinding
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.launch

/**
 *  文件名：MineCancelAccountConfirmUI
 *  创建者: zcy
 *  描述: 确认原因
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineCancelAccountConfirmUI)
class ClearAccountConfirmUI :
    BaseMineUI<UiClearAccountConfirmBinding, SignViewModel>() {

    val cancelReasonAdapter: CancelAccountReasonAdapter by lazy {
        CancelAccountReasonAdapter()
    }

    private var checkList = ArrayList<CancelReasonBeanItem>()
    var reasonStr: String = ""


    override fun initView() {
        binding.mineToolbar.toolbarTitle.text = "申请注销"

//        MineUtils.cancelAccountDeleteContent(binding.delete)

        binding.clearCry.rcyCommonView.adapter = cancelReasonAdapter

        binding.goOnCancelBtn.setOnClickListener {

            var inputReason = binding.otherReason.text.toString()
            if (cancelReasonAdapter.getCheckList().size == 0) {
                showToast("请选择注销原因")
                return@setOnClickListener
            }
            if (cancelReasonAdapter.isOtherReason() && inputReason.isNullOrEmpty()) {
                showToast("请填写注销原因")
                return@setOnClickListener
            }
            var reason: String = reasonStr
            //组装注销原因数据
            reason = if (inputReason.isNotEmpty() && cancelReasonAdapter.isOtherReason()) {
                reasonStr + "${cancelReasonAdapter.data.last().dictLabel}：${inputReason}"
            } else {
                reason.substring(0, reason.length - 1)
            }
            ConfirmTwoBtnPop(this).apply {
                title.apply {
                    text = "是否确认注销账号？"
                    visibility = View.VISIBLE
                }
                contentText.text = "帖子、福币、圈子、经验等级、活动、商城、勋章相关数据将被清除。"
                btnCancel.setOnClickListener {
                    dismiss()
                }
                btnConfirm.setOnClickListener {
                    var b = Bundle()
                    b.putString("value", reason)
                    RouterManger.startARouter(ARouterMyPath.ConfirmCancelAccountUI, b)
                    dismiss()
                    finish()
                }
            }.showPopupWindow()
        }

        binding.cancelBtn.setOnClickListener {
            LiveDataBus.get().with(LiveDataBusKey.MINE_CANCEL_ACCOUNT, Boolean::class.java)
                .postValue(false)
            back()
        }


        viewModel.clearAccountReason.observe(this, Observer {
            it?.let {
                try {
                    //其他放在最后，设置reasonId标识为其他
                    var bean = it?.last()
                    bean.reasonId = -1
                    it.removeAt(it.size - 1)
                    it.add(bean)
                    cancelReasonAdapter.addData(it)
                } catch (e: Exception) {//如报错，直接加载数据
                    cancelReasonAdapter.addData(it)
                }
            }
        })

        //取消注销申请  注销账户成功
        LiveDataBus.get().with(LiveDataBusKey.MINE_CANCEL_ACCOUNT, Boolean::class.java)
            .observe(this,
                Observer {
                    back()
                })
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        lifecycleScope.launch {
            viewModel.cancelAccountReason()
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.clearCry.smartCommonLayout
    }

    override fun hasRefresh(): Boolean {
        return false
    }

    /**
     * 注销原因
     */
    inner class CancelAccountReasonAdapter :
        BaseQuickAdapter<CancelReasonBeanItem, BaseDataBindingHolder<ItemClearAccountReasonBinding>>(
            R.layout.item_clear_account_reason
        ) {

        override fun convert(
            holder: BaseDataBindingHolder<ItemClearAccountReasonBinding>,
            item: CancelReasonBeanItem
        ) {
            holder.dataBinding?.let {
                it.cancelReasonCheck.text = "${item.dictLabel}"
                it.cancelReasonCheck.setOnCheckedChangeListener { _, isChecked ->
                    item.isCheck = isChecked
                    if (item.reasonId == -1) {
                        binding.otherReason.visibility = if (isChecked) View.VISIBLE else View.GONE
                    }
                }
            }
        }

        /**
         * 获取选择的原因
         */
        fun getCheckList(): ArrayList<CancelReasonBeanItem> {
            checkList.clear()
            data.forEach {
                if (it.isCheck) {
                    checkList.add(it) // 添加选择的注销原因
                    if (it.reasonId != -1) { //其他原因
                        reasonStr += "${it.dictLabel},"
                    }
                }
            }
            return checkList
        }

        /**
         * 是否选择其他原因
         */
        fun isOtherReason(): Boolean {
            //判断是否勾选其他
            data.forEach {
                if (it.isCheck && it.reasonId == -1) {
                    return true
                }
            }
            return false
        }
    }
}