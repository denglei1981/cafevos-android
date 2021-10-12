package com.changanford.my.ui

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.UserIdCardBeanItem
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemMineMedalBinding
import com.changanford.my.databinding.UiMineMedalBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：MyVipUI
 *  创建者: zcy
 *  创建日期：2021/10/11 10:25
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MyVipUI)
class MyVipUI : BaseMineUI<UiMineMedalBinding, SignViewModel>() {
    var memberId: String = ""

    val memberAdapter: MedalAdapter by lazy {
        MedalAdapter()
    }

    override fun initView() {
        binding.medalToolbar.toolbarTitle.text = "我的会员身份"
        binding.tvChoose.text = "选择身份"
        binding.tvHint.text = "选择会员身份，在外部展示"

        binding.rcyMedal.rcyCommonView.layoutManager = GridLayoutManager(this, 3)
        binding.rcyMedal.rcyCommonView.adapter = memberAdapter

        binding.btnWear.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                if (memberId.isEmpty()) {
                    showToast("请先选择身份")
                    return
                }
                //佩戴
                viewModel.showUserIdCard(memberId) {
                    it.onSuccess {
                        showToast("展示成功")
                        finish()
                    }
                    it.onWithMsgFailure {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }
        })
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyMedal.smartCommonLayout
    }

    override fun hasRefresh(): Boolean {
        return false
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        viewModel.queryLoginUserIdCardList {
            it?.let {
                completeRefresh(it.data, memberAdapter)
            }
        }
    }

    override fun showEmpty(): View? {
        emptyBinding.viewStatusText.text = "当前还未获得，快去认证会员吧"
        emptyBinding.viewStatusIcon.setImageResource(R.mipmap.ic_vip_no_ex)
        emptyBinding.viewStatusText.textSize = 12f
        return super.showEmpty()
    }


    inner class MedalAdapter :
        BaseQuickAdapter<UserIdCardBeanItem, BaseDataBindingHolder<ItemMineMedalBinding>>(R.layout.item_mine_medal) {
        override fun convert(
            holder: BaseDataBindingHolder<ItemMineMedalBinding>,
            item: UserIdCardBeanItem
        ) {
            holder.dataBinding?.let {
                it.medalIcon.load(item.memberIcon, R.mipmap.ic_def_vip)
                it.medalName.text = item.memberName
                it.checkbox.visibility = if ("1" == item.isShow) View.VISIBLE else View.GONE
                if (item.isShow == "1") {
                    memberId = "${item.memberId}"
                }
            }

            holder.itemView.setOnClickListener {
                if (item.isShow == "1") {
                    return@setOnClickListener
                }
                var isItemShow = item.isShow
                binding.btnWear.isEnabled = true
                memberId = "${item.memberId}"
                data.forEach {
                    it.isShow = "0"
                }
                when (isItemShow) {
                    "0" -> {
                        item.isShow = "1"
                    }
                    "1" -> {
                        item.isShow = "0"
                    }
                }
                notifyDataSetChanged()
            }
        }
    }
}
