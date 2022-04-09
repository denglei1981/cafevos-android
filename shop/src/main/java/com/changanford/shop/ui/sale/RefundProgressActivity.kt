package com.changanford.shop.ui.sale

import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.shop.R
import com.changanford.shop.databinding.BaseRecyclerViewBinding
import com.changanford.shop.databinding.FooterRefundProgressBinding
import com.changanford.shop.databinding.HeaderRefundProgressBinding
import com.changanford.shop.ui.sale.adapter.RefundProgressAdapter
import com.changanford.shop.ui.sale.request.RefundViewModel

/**
 *  退款进度
 * */
@Route(path = ARouterShopPath.RefundProgressActivity)
class RefundProgressActivity : BaseActivity<BaseRecyclerViewBinding, RefundViewModel>() {

    val refundProgressAdapter: RefundProgressAdapter by lazy {
        RefundProgressAdapter()
    }

    override fun initView() {

    }

    override fun initData() {
        val mallMallOrderId = intent.getStringExtra("value")
        if (mallMallOrderId != null) {
            viewModel.getRefundProgress(mallMallOrderId)
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