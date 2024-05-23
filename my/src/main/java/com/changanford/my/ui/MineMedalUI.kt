package com.changanford.my.ui

import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
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

    private val medalAdapter: MedalAdapter by lazy {
        MedalAdapter()
    }

    var medalId: String = ""

    var medalKey: String = ""
    var medalType: String = ""

    override fun initView() {
        binding.medalToolbar.toolbarTitle.text = "我的勋章"
        binding.medalToolbar.toolbar.setNavigationOnClickListener { finish() }

        binding.rcyMedal.rcyCommonView.layoutManager = GridLayoutManager(this, 3)
        binding.rcyMedal.rcyCommonView.adapter = medalAdapter

        binding.btnWear.setOnClickListener {
            if ("去点亮勋章" == binding.btnWear.text.toString().trim()) {
                finish()
            } else {
                if (!TextUtils.isEmpty(medalKey)) {
                    viewModel.wearMedal(medalId, medalKey)
                }

            }
        }

        viewModel.wearMedal.observe(this, Observer {
            showToast(if ("true" == it) "佩戴成功" else it)
        })
    }


    override fun back() {
        LiveDataBus.get().with("refreshNowMedal", String::class.java).postValue(medalId)
        super.back()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        LiveDataBus.get().with("refreshNowMedal", String::class.java).postValue(medalId)
        return super.onKeyDown(keyCode, event)
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
            completeRefresh(it.data, medalAdapter, 0)
            it.data?.let {
                if (it.size > 0) {
                    binding.btnWear.text = "确认佩戴"
                } else {
                    medalAdapter.data.clear()
                    medalAdapter.setEmptyView(R.layout.layout_empty_medai)
                    binding.btnWear.text = "去点亮勋章"
                }
            }
        }
    }

    override fun showEmpty(): View? {
        emptyBinding.viewStatusIcon.setImageResource(R.mipmap.image_common_no_medal)
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
//                it.checkbox.visibility = if ("1" == item.isShow) View.VISIBLE else View.GONE
                it.checkbox.isVisible = true
                it.medalName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if ("1" == item.isShow) R.color.color_1700F4 else R.color.color_16
                    )
                )
                it.checkbox.setImageResource(if ("1" == item.isShow) R.mipmap.ic_checked_ford else R.mipmap.ic_check_ford)
                if (item.isShow == "1") {
                    medalId = item.isShow
                }
            }

            holder.itemView.setOnClickListener {
                if (item.isShow == "1") {
                    return@setOnClickListener
                }
                val isItemShow = item.isShow
                binding.btnWear.isEnabled = true
                medalKey = item.medalKey
                data.forEach {
                    it.isShow = "0"
                }
                when (isItemShow) {
                    "1" -> {
                        item.isShow = "0"
                        medalId = "0"
                    }

                    else -> {
                        item.isShow = "1"
                        medalId = "1"
                    }
                }
                notifyDataSetChanged()
            }
        }
    }
}