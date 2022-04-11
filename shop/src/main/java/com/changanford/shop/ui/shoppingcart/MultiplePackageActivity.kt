package com.changanford.shop.ui.shoppingcart

import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.shop.R
import com.changanford.shop.bean.NoSendSkuData
import com.changanford.shop.databinding.*
import com.changanford.shop.ui.shoppingcart.adapter.MultipleImgsAdapter
import com.changanford.shop.ui.shoppingcart.adapter.MultipleNoSendImgsAdapter
import com.changanford.shop.ui.shoppingcart.adapter.MultiplePackageAdapter
import com.changanford.shop.ui.shoppingcart.request.MultiplePackageViewModel
import com.changanford.shop.ui.shoppingcart.request.ShoppingCartViewModel
import com.changanford.shop.view.TopBar
import com.google.gson.Gson

/**
 *  多包裹发货
 * */
@Route(path = ARouterShopPath.MultiplePackageActivity)
class MultiplePackageActivity :
    BaseActivity<ActivityMultiplePackageBinding, MultiplePackageViewModel>() {

    val multiplePackageAdapter: MultiplePackageAdapter by lazy {
        MultiplePackageAdapter()
    }

    companion object {
        fun start(orderNo: String) {
            JumpUtils.instans?.jump(127, orderNo)
        }
    }

    var headNewBinding: HeaderMultiplePackageBinding? = null
    private fun addHeadView() {
        if (headNewBinding == null) {
            headNewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.header_multiple_package,
                binding.recyclerView,
                false
            )
            headNewBinding?.let {
                multiplePackageAdapter.addHeaderView(it.root, 0)
            }
        }
    }

    override fun initView() {
        binding.recyclerView.adapter = multiplePackageAdapter
        binding.topbar.setOnBackClickListener(object : TopBar.OnBackClickListener {
            override fun onBackClick() {
                finish()
            }
        })
    }

    override fun initData() {
        val orderNo = intent.getStringExtra("value")
        if (orderNo != null) {
            viewModel.getMultiplePackInfo(orderNo)
        }
        addHeadView()

    }

    var footerBinding: FooterMultiplePackageBinding? = null
    private fun addFooterView(list: MutableList<NoSendSkuData>) {
        if (footerBinding == null) {
            footerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.footer_multiple_package,
                binding.recyclerView,
                false
            )
            footerBinding?.let {
                multiplePackageAdapter.addFooterView(it.root)
                val multipleImgsAdapter = MultipleNoSendImgsAdapter()
                multipleImgsAdapter.setNewInstance(list)
                it.rvShopping.adapter = multipleImgsAdapter
                var countShop: Int = 0
                list.forEach { sku ->
                    run {
                        countShop += sku.buyNum
                    }
                }
                it.tvPackageName.text = "以下商品待发货"
                it.tvPackageState.text = "待发货"
                it.tvMoreInfo.text = "共${countShop}商品"
            }

        }

    }

    override fun observe() {
        super.observe()
        viewModel.packMainDataLiveData.observe(this, Observer {

            multiplePackageAdapter.setNewInstance(it.logisticsItems)
            if (it.noSendSkuDTOs != null && it.noSendSkuDTOs!!.size > 0) {
                addFooterView(it.noSendSkuDTOs!!)
            }
        })
    }
}