package com.changanford.shop.ui.sale

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.PayShowBean
import com.changanford.common.bean.RefundBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.showTotalTag
import com.changanford.common.utilext.logE
import com.changanford.shop.R
import com.changanford.shop.bean.RefundProgressBean
import com.changanford.shop.databinding.ActivityRefundProgressBinding
import com.changanford.shop.databinding.FooterRefundProgressBinding
import com.changanford.shop.databinding.HeaderRefundProgressBinding
import com.changanford.shop.ui.sale.adapter.RefundImgsAdapter
import com.changanford.shop.ui.sale.adapter.RefundProgressAdapter
import com.changanford.shop.ui.sale.request.RefundViewModel
import com.changanford.shop.view.TopBar
import com.google.gson.Gson

/**
 *  退款进度整单退---> 未发货前
 * */
@Route(path = ARouterShopPath.RefundProgressActivity)
class RefundProgressActivity : BaseActivity<ActivityRefundProgressBinding, RefundViewModel>() {
    //
    val refundImgsAdapter: RefundImgsAdapter by lazy {
        RefundImgsAdapter()
    }

    companion object {
        fun start(mallMallRefundId:String){
            JumpUtils.instans?.jump(124, mallMallRefundId)
        }
    }
    val refundProgressAdapter: RefundProgressAdapter by lazy {
        RefundProgressAdapter(viewModel)
    }

    override fun initView() {
        binding.tobBar.setOnBackClickListener(object : TopBar.OnBackClickListener {
            override fun onBackClick() {
                onBackPressed()
            }
        })
        binding.tobBar.setTitle("退款进度")
        binding.recyclerView.adapter = refundProgressAdapter
    }

    override fun initData() {
        val mallMallRefundId = intent.getStringExtra("value")
        if (mallMallRefundId != null) {
            viewModel.getRefundProgress(mallMallRefundId)
            addHeadView()
            addFooterView()
        }
    }

    override fun observe() {
        super.observe()
        viewModel.refundProgressLiveData.observe(this, Observer {
            refundProgressAdapter.refundStatus = it.refundStatus // 当前状态
            refundProgressAdapter.setNewInstance(it.refundList)
            showFooterAndHeader(it)
        })
        viewModel.cancelRefundLiveData.observe(this, Observer {
            // 撤销退款申请成功
            this.finish()
        })
    }

    fun showFooterAndHeader(refundProgressBean: RefundProgressBean) {
        headNewBinding?.let {
            viewModel.StatusEnum("MallRefundStatusEnum", refundProgressBean.refundStatus, it.tvTips)
            when (refundProgressBean.refundStatus) {
                "FINISH" -> {
                    it.tvSubTips.visibility = View.VISIBLE
                    showTotalTag(
                        this,
                        it.tvSubTips,
                        PayShowBean(refundProgressBean.rmbRefund, refundProgressBean.fbRefund),
                        false
                    )
                }
                else -> {
                    it.tvSubTips.visibility = View.GONE
//                    showTotalTag(
//                        this,
//                        it.tvSubTips,
//                        PayShowBean(refundProgressBean.rmbRefund, refundProgressBean.fbRefund),
//                        false
//                    )
                }
            }
        }
        footerBinding?.let { ft ->
            ft.layoutRefundInfo.tvReasonNum.text = refundProgressBean.refundNo
            viewModel.StatusEnum(
                "MallRefundMethodEnum",
                refundProgressBean.refundMethod,
                ft.layoutRefundInfo.tvRefundType
            )

            refundProgressBean.refundReason?.let {
                viewModel.StatusEnum(
                    "MallRefundReasonEnum",
                    it,
                    ft.layoutRefundInfo.tvResonShow
                )
            }
            if(TextUtils.isEmpty(refundProgressBean.refundReason)){
                ft.layoutRefundInfo.tvResonShow.text="--"
            }

            showTotalTag(
                this,
                ft.layoutRefundInfo.tvRefundMoney,
                PayShowBean(refundProgressBean.rmbRefundApply, refundProgressBean.fbRefundApply),
                false
            )
            when (refundProgressBean.refundStatus) {
                "ON_GOING" -> {
                    ft.tvHandle.visibility = View.VISIBLE
                    ft.tvHandle.text = "撤销退款申请"
                    ft.tvHandle.setOnClickListener {
                        // 撤销退款申请
                        viewModel.cancelRefund(refundProgressBean.mallMallRefundId)
                    }
                }
                "CLOSED" -> { // 退款关闭
                    ft.tvHandle.visibility = View.VISIBLE
                    ft.tvHandle.text = "申请退款"
                    ft.tvHandle.setOnClickListener {
                        val gson = Gson()
                        val refundBean =
                            RefundBean(
                                refundProgressBean.orderNo,
                                refundProgressBean.fbRefundApply,
                                refundProgressBean.rmbRefundApply,
                                "allOrderRefund"
                            )
                        val refundJson = gson.toJson(refundBean)
                        refundJson.toString().logE()
                        JumpUtils.instans?.jump(121, refundJson)
                    }
                }
                else -> {
                    ft.tvHandle.visibility = View.GONE
                }
            }

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

    var footerBinding: FooterRefundProgressBinding? = null
    private fun addFooterView() {
        if (footerBinding == null) {
            footerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.footer_refund_progress,
                binding.recyclerView,
                false
            )
            footerBinding?.let {
                refundProgressAdapter.addFooterView(it.root)
            }
        }

    }
}