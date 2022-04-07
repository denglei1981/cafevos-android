package com.changanford.shop.ui.order

import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.utilext.toast
import com.changanford.shop.R
import com.changanford.shop.bean.InvoiceInfo
import com.changanford.shop.databinding.ActivityInvoiceInfoBinding
import com.changanford.shop.databinding.ActivityOrderDetailsBinding
import com.changanford.shop.ui.order.request.GetInvoiceViewModel
import com.changanford.shop.view.TopBar
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson

// 发票 信息
@Route(path = ARouterShopPath.InvoiceActivity)
class InvoiceActivity : BaseActivity<ActivityInvoiceInfoBinding, GetInvoiceViewModel>() {

    override fun initView() {
        binding.topbar.setOnBackClickListener(object : TopBar.OnBackClickListener {
            override fun onBackClick() {
                onBackPressed()
            }
        })

    }

    var personName:String =""
    var companyName:String ="" // 单位名称
    var taxpayerName:String ="" // 纳税人识别号
    override fun initData() {
        val invoiceInfoStr = intent.getStringExtra("value")
        val gson = Gson()
        val invoiceInfo = gson.fromJson<InvoiceInfo>(invoiceInfoStr, InvoiceInfo::class.java)

        binding.tvAddress.text = invoiceInfo.addressInfo
        binding.tvUserInfo.text = invoiceInfo.userName.plus(":").plus(invoiceInfo.phone)
        showPerson()
        binding.tvGetInvoice.setOnClickListener { // 申请开票
            if(canGetInvoice()){
                if(binding.rbPerson.isChecked){ // 选择的开个人票
                    viewModel.getUserInvoiceAdd(invoiceInfo.addressId,"个人",personName,invoiceInfo.invoiceRmb,invoiceInfo.mallMallOrderId,invoiceInfo.mallMallOrderNo)
                }
                if(binding.rbCompany.isChecked){ // 选择的开单位票
                    viewModel.getUserInvoiceAdd(invoiceInfo.addressId,"单位",companyName,invoiceInfo.invoiceRmb,invoiceInfo.mallMallOrderId,invoiceInfo.mallMallOrderNo,taxpayerName)
                }
            }

        }
        binding.rbPerson.isChecked = true
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.rbPerson.id -> {
                    showPerson()
                }
                binding.rbCompany.id -> {
                    showCompany()
                }

            }
        }
    }

    private fun showCompany() {
        binding.llPerson.visibility = View.GONE
        binding.llCompany.visibility = View.VISIBLE
        binding.llTaxpayer.visibility = View.VISIBLE
        binding.rbPerson.background =
            ContextCompat.getDrawable(this, R.drawable.shape_search_bg_gray_f4)
        binding.rbCompany.background = ContextCompat.getDrawable(this, R.drawable.shape_bg_gray_e5)
        binding.rbPerson.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.rbCompany.setTextColor(ContextCompat.getColor(this, R.color.color_00095B))
    }

    private fun showPerson() {
        binding.llPerson.visibility = View.VISIBLE
        binding.llCompany.visibility = View.GONE
        binding.llTaxpayer.visibility = View.GONE
        binding.rbPerson.background = ContextCompat.getDrawable(this, R.drawable.shape_bg_gray_e5)
        binding.rbCompany.background =
            ContextCompat.getDrawable(this, R.drawable.shape_search_bg_gray_f4)
        binding.rbPerson.setTextColor(ContextCompat.getColor(this, R.color.color_00095B))
        binding.rbCompany.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

    fun canGetInvoice(): Boolean {
        if (binding.rbPerson.isChecked) { // 选择的是个人开票
            personName=  binding.etPersonName.text.toString()
            if(TextUtils.isEmpty(personName)){
                "请输入个人姓名".toast()
                return false
            }
        }
        if(binding.rbCompany.isChecked){ // 选择的是单位开票
            companyName =binding.etComponyName.text.toString()
            if(TextUtils.isEmpty(companyName)){
                "请输入个公司名称".toast()
                return false
            }
            taxpayerName =binding.etComponyTaxpayer.text.toString()
            if(TextUtils.isEmpty(taxpayerName)){
                "请输入纳税人识别号".toast()
                return false
            }
        }
        return true
    }

    override fun observe() {
        super.observe()
        viewModel.invoiceLiveData.observe(this, Observer {
            this.finish()
        })
    }
}