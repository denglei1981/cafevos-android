package com.changanford.shop.ui.sale

import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.PayShowBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.showTotalTag
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.FlowLayoutManager
import com.changanford.shop.bean.RefundProgressBean
import com.changanford.shop.databinding.*
import com.changanford.shop.ui.sale.adapter.RefundImgsAdapter
import com.changanford.shop.ui.sale.adapter.RefundProgressAdapter
import com.changanford.shop.ui.sale.request.RefundViewModel
import com.changanford.shop.ui.shoppingcart.adapter.GoodsAttributeAdapter
import com.changanford.shop.view.TopBar
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

/**
 *  申请退款  已发货， 仅退款 ，退款退货
 * */
@Route(path = ARouterShopPath.RefundProgressHasShopActivity)
class RefundProgressHasShopActivity :
    BaseActivity<ActivityRefundProgressBinding, RefundViewModel>(), OnRefreshListener {


    companion object {
        fun start(mallOrderSkuId: String) {
            JumpUtils.instans?.jump(126, mallOrderSkuId)
        }
    }

    val refundProgressAdapter: RefundProgressAdapter by lazy {
        RefundProgressAdapter(viewModel)
    }
//
    val refundImgsAdapter: RefundImgsAdapter by lazy {
        RefundImgsAdapter()
    }

    override fun initView() {
        binding.tobBar.setOnBackClickListener(object : TopBar.OnBackClickListener {
            override fun onBackClick() {
                onBackPressed()
            }
        })
        binding.recyclerView.adapter = refundProgressAdapter
        binding.smartLayout.setEnableLoadMore(false)
        binding.smartLayout.setOnRefreshListener(this)
    }

    var mallMallOrderSkuId: String? = ""
    override fun initData() {
        mallMallOrderSkuId = intent.getStringExtra("value")
        if (mallMallOrderSkuId != null) {
            viewModel.getRefundProgress("", mallMallOrderSkuId!!)
            addHeadView()
            addFooterView()
        }
    }

    override fun observe() {
        super.observe()
        viewModel.refundProgressLiveData.observe(this, Observer {
            binding.smartLayout.finishRefresh()
            refundProgressAdapter.refundStatus = it.refundStatus // 当前状态
            refundProgressAdapter.setNewInstance(it.refundList)
            showFooterAndHeader(it)

        })
        viewModel.cancelRefundLiveData.observe(this, Observer {
            // 撤销退款申请成功
            this.finish()
        })
        LiveDataBus.get().with(LiveDataBusKey.FILL_IN_LOGISTICS).observe(this, Observer {
             // 刷新 进度
            mallMallOrderSkuId?.let {
                viewModel.getRefundProgress("", it)
            }
        })

    }

    fun showFooterAndHeader(refundProgressBean: RefundProgressBean) {
        headNewBinding?.let {
            viewModel.StatusEnum("MallRefundStatusEnum", refundProgressBean.refundStatus, it.tvTips)
            when (refundProgressBean.refundStatus) {
                "SUCCESS" -> {
                    it.tvSubTips.visibility = View.VISIBLE
                }
                else -> {
                    it.tvSubTips.visibility = View.GONE
                    showTotalTag(
                        this,
                        it.tvSubTips,
                        PayShowBean(refundProgressBean.rmbRefund, refundProgressBean.fbRefund),
                        false
                    )
                }
            }
        }
        footerBinding?.let { ft ->
            ft.layoutRefundInfo.tvReasonNum.text = refundProgressBean.refundNo
//            viewModel.StatusEnum(
//                "MallRefundMethodEnum",
//                refundProgressBean.refundMethod,
//                ft.layoutRefundInfo.tvRefundType
//            )
            viewModel.StatusEnum(
                "MallRefundReasonEnum",
                refundProgressBean.refundReason,
                ft.layoutRefundInfo.tvResonShow
            )
            showTotalTag(
                this,
                ft.layoutRefundInfo.tvRefundMoney,
                PayShowBean(refundProgressBean.rmbRefundApply, refundProgressBean.fbRefundApply),
                false
            )

            when (refundProgressBean.refundMethod) {
                "ONLY_COST" -> { // 仅退款
                    binding.tobBar.setTitle("退款进度")
                    ft.tvInputOrder.visibility = View.GONE
                    ft.layoutRefundInfo.tvRefundType.text = "仅退款"
                }
                "CONTAIN_GOODS" -> {
                    binding.tobBar.setTitle("退款进度")
                    ft.tvInputOrder.visibility = View.VISIBLE
                    ft.layoutRefundInfo.tvRefundType.text = "退货退款"
                }

            }
            when (refundProgressBean.refundStatus) {
                "ON_GOING" -> {
                    ft.tvHandle.visibility = View.VISIBLE
                    ft.tvInputOrder.visibility = View.VISIBLE
                    ft.tvHandle.text = "撤销退款申请"
                    ft.tvHandle.setOnClickListener {
                        // 撤销退款申请
                        viewModel.cancelRefund(refundProgressBean.mallMallRefundId)
                    }
                    ft.tvInputOrder.setOnClickListener {
                        // 填写物流信息
                        val intent = Intent()
                        intent.putExtra("value", refundProgressBean.mallMallRefundId)
                        intent.setClass(
                            this@RefundProgressHasShopActivity,
                            RefundLogisticsActivity::class.java
                        )
                        startActivity(intent)
                    }
                    when (refundProgressBean.refundDetailStatus) {
                        "WAIT_CHECK","OVERTIME","WAIT_RECEIVE_RETURNS" -> {
                            ft.tvInputOrder.visibility = View.GONE
                        }
                        "CANCELD_REFUND"->{
                            ft.tvInputOrder.visibility = View.GONE
                            ft.tvHandle.visibility = View.GONE
                        }
                    }
                }

                "CLOSED" -> { // 退款关闭
                    ft.tvInputOrder.visibility = View.GONE
                    ft.tvHandle.visibility = View.GONE
                }
                else -> {
                    ft.tvInputOrder.visibility = View.GONE
                    ft.tvHandle.visibility = View.GONE
                }
            }
            refundProgressBean.sku?.let { list ->
                GlideUtils.loadBD(list.skuImg, ft.layoutRefundInfo.layoutShop.imgGoodsCover)
                val goodsAttributeAdapter = GoodsAttributeAdapter()
                goodsAttributeAdapter.setList(list.getTagList())

                val layoutManager = FlowLayoutManager(this, false, true)
                ft.layoutRefundInfo.layoutShop.recyclerView.layoutManager = layoutManager
                ft.layoutRefundInfo.layoutShop.recyclerView.adapter = goodsAttributeAdapter
                ft.layoutRefundInfo.layoutShop.tvNum.text = list.getSaleNum()
                ft.layoutRefundInfo.layoutShop.tvGoodsTitle.text = list.spuName

            }
            if (TextUtils.isEmpty(refundProgressBean.refundDescText)) {
                ft.layoutRefundInfo.tvContent.visibility = View.GONE
            } else {
                ft.layoutRefundInfo.tvContent.visibility = View.VISIBLE
            }
            ft.layoutRefundInfo.tvContent.text = refundProgressBean.refundDescText

            ft.layoutRefundInfo.rvImg.adapter=refundImgsAdapter
            refundImgsAdapter.setNewInstance(refundProgressBean.refundDescImgs)
        }
    }

    var headNewBinding: HeaderRefundProgressBinding? = null
    private fun addHeadView() {
        if (headNewBinding == null) {
            headNewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.header_refund_progress,
                binding.recyclerView,
                false
            )
            headNewBinding?.let {
                refundProgressAdapter.addHeaderView(it.root, 0)
            }
        }
    }

    var footerBinding: FooterRefundProgressHasShopBinding? = null
    private fun addFooterView() {
        if (footerBinding == null) {
            footerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.footer_refund_progress_has_shop,
                binding.recyclerView,
                false
            )
            footerBinding?.let {
                refundProgressAdapter.addFooterView(it.root)
            }
        }

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mallMallOrderSkuId?.let {
            viewModel.getRefundProgress("", it)
        }

    }
}