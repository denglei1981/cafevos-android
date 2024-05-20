package com.changanford.shop.ui.sale

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.RefundOrderItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.shop.databinding.ActivityAfterSaleBinding
import com.changanford.shop.ui.order.adapter.RefundOrderItemAdapter
import com.changanford.shop.view.TopBar
import com.google.gson.Gson

/**
 * 商品售后 --- 已发货 -- 并且到货了。
 * */
@Route(path = ARouterShopPath.AfterSaleActivity)
class AfterSaleActivity : BaseActivity<ActivityAfterSaleBinding, BaseViewModel>() {

    private val orderDetailsItemV2Adapter: RefundOrderItemAdapter by lazy {
        RefundOrderItemAdapter()
    }

    override fun initView() {
        binding.layoutTop.setOnBackClickListener(object : TopBar.OnBackClickListener {
            override fun onBackClick() {
                onBackPressed()
            }
        })
        binding.layoutTop.setOnRightClickListener(object : TopBar.OnRightClickListener {
            override fun onRightClick() {
                JumpUtils.instans?.jump(11)
            }
        })
    }

    override fun initData() {
        val orderString = intent.getStringExtra("value")
        val gson = Gson()
        // 具体要退的商品
        val orderItemBean = gson.fromJson(orderString, RefundOrderItemBean::class.java)
        val list = arrayListOf<RefundOrderItemBean>()
        list.add(orderItemBean)
        binding.rvShopping.adapter = orderDetailsItemV2Adapter
        orderDetailsItemV2Adapter.setNewInstance(list)
        binding.tvOnlyMoney.setOnClickListener {
            orderItemBean.singleRefundType = "ONLY_COST"
            val gson = Gson()
            val toJson = gson.toJson(orderItemBean)
            JumpUtils.instans?.jump(125, toJson)
        }
        binding.tvMoneyShop.setOnClickListener {
            orderItemBean.singleRefundType = "CONTAIN_GOODS"
            val gson = Gson()
            val toJson = gson.toJson(orderItemBean)
            JumpUtils.instans?.jump(125, toJson)
        }
    }

    override fun observe() {
        super.observe()
        LiveDataBus.get().with(LiveDataBusKey.SINGLE_REFUND).observe(this, Observer {
            this.finish()
        })
    }
}