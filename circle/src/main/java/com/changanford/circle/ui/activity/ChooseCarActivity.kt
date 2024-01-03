package com.changanford.circle.ui.activity

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.ChooseCarAdapter
import com.changanford.circle.databinding.ActivityChooseCarBinding
import com.changanford.circle.viewmodel.ChooseCarViewModel
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey

/**
 *Author lcw
 *Time on 2024/1/3
 *Purpose 选择车型
 */
@Route(path = ARouterCirclePath.ChooseCarActivity)
class ChooseCarActivity : BaseLoadSirActivity<ActivityChooseCarBinding, ChooseCarViewModel>() {

    private val adapter by lazy { ChooseCarAdapter() }

    override fun onRetryBtnClick() {
        initData()
    }

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.root, this)
        setLoadSir(binding.ryCar)
        binding.title.apply {
            barTvTitle.text = "请选择车型"
            barTvOther.isVisible = true
            barTvOther.text = "确定"
            barTvOther.textSize = 15f
            barTvOther.setTextColor(
                ContextCompat.getColor(
                    this@ChooseCarActivity,
                    R.color.color_1700F4
                )
            )
            barImgBack.setOnClickListener { finish() }
            barTvOther.setOnClickListener {
                LiveDataBus.get().with(LiveDataBusKey.CHOOSE_CAR_POST)
                    .postValue(adapter.data[adapter.checkPosition].spuName)
                finish()
            }
        }
        binding.ryCar.adapter = adapter
        initListener()
    }

    private fun initListener() {
        adapter.setOnItemClickListener { _, _, position ->
            adapter.checkPosition = position
            adapter.notifyDataSetChanged()
        }
    }

    override fun initData() {
        viewModel.getMoreCar()
        viewModel.carMoreInfoBean.observe(this) {
            if (it?.carModels.isNullOrEmpty()) {
                showFailure("没有数据")
            } else {
                adapter.setList(it?.carModels)
                showContent()
            }
        }
    }
}