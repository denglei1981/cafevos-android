package com.changanford.shop.ui.order

import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.TimeUtils
import com.changanford.shop.databinding.ActivityInvoiceInfoBinding
import com.changanford.shop.databinding.ActivityInvoiceLookBinding
import com.changanford.shop.databinding.ActivityOrderDetailsBinding
import com.changanford.shop.ui.order.request.GetInvoiceViewModel
import com.changanford.shop.view.TopBar
import com.changanford.shop.viewmodel.OrderViewModel

//  查看 发票
@Route(path = ARouterShopPath.InvoiceLookActivity)
class InvoiceLookActivity : BaseActivity<ActivityInvoiceLookBinding, GetInvoiceViewModel>() {
    override fun initView() {
        binding.topbar.setOnBackClickListener(object : TopBar.OnBackClickListener {
            override fun onBackClick() {
                onBackPressed()
            }
        })

    }

    override fun initData() {
        val mallOrderNo = intent.getStringExtra("value")
        mallOrderNo?.let {
            viewModel.getUserInvoiceDetail(mallMallOrderNo = it)
        }
    }

    override fun observe() {
        super.observe()
        viewModel.invoiceDetailsLiveData.observe(this, Observer {

            if(it!=null){
                when (it.invoiceStatus) {
                    "YES" -> {
                        binding.ivAddress.visibility = View.VISIBLE
                        binding.tvInvoiceStates.text = "已开票"
                        binding.tvInvoiceMoney.text = "开票金额: ￥${it.invoiceRmb}"
                    }
                    "NO" -> {
                        binding.ivAddress.visibility = View.GONE
                        binding.tvInvoiceStates.text = "未开票"
                        binding.tvInvoiceMoney.text = "预计开票金额: ￥${it.invoiceRmb}"
                        binding.llInvoiceTime.setSecondText("未开票")
                    }
                }
                binding.llInvoiceTaitou.setSecondText(it.invoiceHeader)
                binding.llInvoiceContent.setSecondText("商品明细")
                binding.llInvoiceTaitouName.setSecondText(it.invoiceHeaderName)
                binding.llInvoiceApply.setSecondText(TimeUtils.MillisToStr(it.applyTime))
                when(it.invoiceType){
                    1->{
                        binding.llInvoiceType.setSecondText("增值税纸质普通发票")
                    }
                }
            }


        })
    }
}