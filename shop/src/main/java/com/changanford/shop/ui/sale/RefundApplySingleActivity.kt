package com.changanford.shop.ui.sale

import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.BackEnumBean
import com.changanford.common.bean.PayShowBean
import com.changanford.common.bean.RefundOrderItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.showTotalTag
import com.changanford.shop.databinding.ActivityOnlyRefundBinding
import com.changanford.shop.ui.order.adapter.RefundOrderItemAdapter
import com.changanford.shop.ui.shoppingcart.dialog.RefundResonDialog
import com.changanford.shop.view.TopBar
import com.google.gson.Gson
import java.math.BigDecimal

/**
 *   单个sku 退款 申请
 * */
@Route(path = ARouterShopPath.RefundApplySingleActivity)
class RefundApplySingleActivity : BaseActivity<ActivityOnlyRefundBinding, BaseViewModel>() {
    var backEnumBean: BackEnumBean? = null
    val orderDetailsItemV2Adapter: RefundOrderItemAdapter by lazy {
        RefundOrderItemAdapter()
    }

    override fun initView() {
        binding.layoutTop.setOnBackClickListener(object : TopBar.OnBackClickListener {
            override fun onBackClick() {
                onBackPressed()
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
        binding.addSubtractView.setNumber(orderItemBean.buyNum, false)
        binding.addSubtractView.setMax(orderItemBean.buyNum,true)
        when (orderItemBean.singleRefundType) {
            "ONLY_COST" -> {
                binding.layoutTop.setTitle("仅退款")
            }
            "CONTAIN_GOODS" -> {
                binding.layoutTop.setTitle("退货退款")
            }
        }
        binding.rvShopping.adapter = orderDetailsItemV2Adapter
        orderDetailsItemV2Adapter.setNewInstance(list)
        binding.tvReason.setOnClickListener {
            // 退款原因
            RefundResonDialog(this, object : RefundResonDialog.CallMessage {
                override fun message(reson: BackEnumBean) {
                    binding.tvReason.text = reson.message
                    backEnumBean = reson
                }
            }).show()
        }
        val payShowBean = PayShowBean()
        val finallyNumber = binding.addSubtractView.getNumber() // 最终的数量
        if (TextUtils.isEmpty(orderItemBean.sharedRmb) && !TextUtils.isEmpty(orderItemBean.sharedFb)) {
            payShowBean.payFb = BigDecimal(orderItemBean.sharedFb).multiply(BigDecimal(finallyNumber)).toString()
        }

        if (TextUtils.isEmpty(orderItemBean.sharedFb) && !TextUtils.isEmpty(orderItemBean.sharedRmb)) {

            payShowBean.payRmb = BigDecimal(orderItemBean.sharedRmb).multiply(BigDecimal(finallyNumber)).toString()
        }
        if (!TextUtils.isEmpty(orderItemBean.sharedFb) && !TextUtils.isEmpty(orderItemBean.sharedRmb)) {
            payShowBean.payFb =  BigDecimal(orderItemBean.sharedFb).multiply(BigDecimal(finallyNumber)).toString()
            payShowBean.payRmb =BigDecimal(orderItemBean.sharedRmb).multiply(BigDecimal(finallyNumber)).toString()
        }
        showTotalTag(this, binding.tvRefundMoney, payShowBean, false)
    }
}