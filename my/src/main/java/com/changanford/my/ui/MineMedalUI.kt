package com.changanford.my.ui

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemMineMedalBinding
import com.changanford.my.databinding.UiMineMedalBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：MineMedalUI
 *  创建者: zcy
 *  创建日期：2021/9/22 10:17
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineMedalUI)
class MineMedalUI : BaseMineUI<UiMineMedalBinding, SignViewModel>() {

    val medalAdapter: MedalAdapter by lazy {
        MedalAdapter()
    }

    var medalId: String = ""

    override fun initView() {
        binding.medalToolbar.toolbarTitle.text = "我的勋章"

        binding.rcyMedal.rcyCommonView.layoutManager = GridLayoutManager(this, 3)
        binding.rcyMedal.rcyCommonView.adapter = medalAdapter

        binding.btnWear.setOnClickListener {
            viewModel.wearMedal(medalId, "1")
        }

        viewModel.wearMedal.observe(this, Observer {
            showToast(if ("true" == it) "佩戴成功" else it)
        })
    }


    override fun hasRefresh(): Boolean {
        return false
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyMedal.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        viewModel.oneselfMedal() {
            completeRefresh(it?.data, medalAdapter, 0)
            it?.data?.let {
                if (it.size > 0) {
                    binding.btnWear.text = "佩戴"
                } else {
                    binding.btnWear.text = "去点亮勋章"
                    binding.btnWear.visibility = View.GONE
                }
            }
        }
    }

    override fun showEmpty(): View? {
        emptyBinding.viewStatusIcon.setImageResource(R.mipmap.ic_medal_ex)
        emptyBinding.viewStatusText.text = "当前还未获得勋章，快去点亮勋章吧"
        emptyBinding.viewStatusText.textSize = 12f
        return super.showEmpty()
    }

    inner class MedalAdapter :
        BaseQuickAdapter<MedalListBeanItem, BaseDataBindingHolder<ItemMineMedalBinding>>(R.layout.item_mine_medal) {
        override fun convert(
            holder: BaseDataBindingHolder<ItemMineMedalBinding>,
            item: MedalListBeanItem
        ) {
            holder.dataBinding?.let {
                it.medalIcon.load(item.medalImage, R.mipmap.ic_medal_ex)
                it.medalName.text = item.medalName
                it.checkbox.visibility = if ("1" == item.isShow) View.VISIBLE else View.GONE
                if (item.isShow == "1") {
                    medalId = item.medalId
                }
            }

            holder.itemView.setOnClickListener {
                if (item.isShow == "1") {
                    return@setOnClickListener
                }
                var isItemShow = item.isShow
                binding.btnWear.isEnabled = true
                medalId = item.medalId
                data.forEach {
                    it.isShow = "0"
                }
                when (isItemShow) {
                    "1" -> {
                        item.isShow = "0"
                    }
                    else -> {
                        item.isShow = "1"
                    }
                }
                notifyDataSetChanged()
            }
        }
    }
}