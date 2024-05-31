package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.bean.ShopAddressInfoBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toast
import com.changanford.shop.R
import com.changanford.shop.bean.InvoiceInfo
import com.changanford.shop.databinding.ActivityInvoiceInfoBinding
import com.changanford.shop.ui.order.request.GetInvoiceViewModel
import com.changanford.shop.view.TopBar
import com.google.gson.Gson
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

// 发票 信息
@Route(path = ARouterShopPath.InvoiceActivity)
class InvoiceActivity : BaseActivity<ActivityInvoiceInfoBinding, GetInvoiceViewModel>() {

    companion object {
        fun start(invoiceInfo: InvoiceInfo) {
            val gson = Gson()
            val invoiceStr = gson.toJson(invoiceInfo)
            JumpUtils.instans?.jump(120, invoiceStr)
        }
    }

    override fun initView() {
        binding.topbar.setOnBackClickListener(object : TopBar.OnBackClickListener {
            override fun onBackClick() {
                onBackPressed()
            }
        })

    }

    var personName: String = ""
    var companyName: String = "" // 单位名称
    var taxpayerName: String = "" // 纳税人识别号
    var addressBeanItem: AddressBeanItem? = null
    lateinit var invoiceInfo: InvoiceInfo

    @SuppressLint("CheckResult")
    override fun initData() {
        val invoiceInfoStr = intent.getStringExtra("value")
        val gson = Gson()
        invoiceInfo = gson.fromJson(invoiceInfoStr, InvoiceInfo::class.java)

        showPerson()

        binding.tvGetInvoice.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (canGetInvoice()) {
                    if (binding.rbPerson.isChecked) { // 选择的开个人票
                        invoiceInfo.invoiceRmb?.let { rmb ->
                            val phone = binding.etPhone.text.toString()
                            val email = binding.etEmail.text.toString()
                            if (email.isNullOrEmpty()) {
                                "请输入邮箱".toast()
                                return@subscribe
                            }
                            viewModel.getUserInvoiceAdd(
                                invoiceInfo.addressId,
                                "个人",
                                personName,
                                rmb,
                                invoiceInfo.mallMallOrderId,
                                invoiceInfo.mallMallOrderNo,
                                email = email,
                                phone = phone,
                            )

                        }

                    }
                    if (binding.rbCompany.isChecked) {
                        val phone = binding.etPhone.text.toString()
                        val email = binding.etEmail.text.toString()
                        if (email.isNullOrEmpty()) {
                            "请输入邮箱".toast()
                            return@subscribe
                        }
                        // 选择的开单位票
                        invoiceInfo.invoiceRmb?.let { rmb ->
                            viewModel.getUserInvoiceAdd(
                                invoiceInfo.addressId,
                                "单位",
                                companyName,
                                rmb,
                                invoiceInfo.mallMallOrderId,
                                invoiceInfo.mallMallOrderNo,
                                email = email,
                                phone = phone,
                                taxpayerName
                            )
                        }

                    }
                }
            }, {})
//        binding.tvGetInvoice.setOnClickListener { // 申请开票
//            if (canGetInvoice()) {
//                if (binding.rbPerson.isChecked) { // 选择的开个人票
//                    viewModel.getUserInvoiceAdd(
//                        invoiceInfo.addressId,
//                        "个人",
//                        personName,
//                        invoiceInfo.invoiceRmb,
//                        invoiceInfo.mallMallOrderId,
//                        invoiceInfo.mallMallOrderNo
//                    )
//                }
//                if (binding.rbCompany.isChecked) { // 选择的开单位票
//                    viewModel.getUserInvoiceAdd(
//                        invoiceInfo.addressId,
//                        "单位",
//                        companyName,
//                        invoiceInfo.invoiceRmb,
//                        invoiceInfo.mallMallOrderId,
//                        invoiceInfo.mallMallOrderNo,
//                        taxpayerName
//                    )
//                }
//            }
//
//        }
        binding.conAddress.setOnClickListener {
            JumpUtils.instans?.jump(20, "2")

        }
        binding.tvNotInvoice.setOnClickListener {
            onBackPressed()
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
        viewModel.getAddressList()
    }

    private fun showCompany() {
        binding.llPerson.visibility = View.GONE
        binding.llCompany.visibility = View.VISIBLE
        binding.llTaxpayer.visibility = View.VISIBLE
        binding.rbPerson.background =
            ContextCompat.getDrawable(this, R.drawable.shape_search_bg_gray_f4)
        binding.rbCompany.background = ContextCompat.getDrawable(this, R.drawable.shape_bg_gray_e5)
        binding.rbPerson.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.rbCompany.setTextColor(ContextCompat.getColor(this, R.color.color_1700f4))
    }

    private fun showPerson() {
        binding.llPerson.visibility = View.VISIBLE
        binding.llCompany.visibility = View.GONE
        binding.llTaxpayer.visibility = View.GONE
        binding.rbPerson.background = ContextCompat.getDrawable(this, R.drawable.shape_bg_gray_e5)
        binding.rbCompany.background =
            ContextCompat.getDrawable(this, R.drawable.shape_search_bg_gray_f4)
        binding.rbPerson.setTextColor(ContextCompat.getColor(this, R.color.color_1700f4))
        binding.rbCompany.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

    fun canGetInvoice(): Boolean {
        if (binding.rbPerson.isChecked) { // 选择的是个人开票
            personName = binding.etPersonName.text.toString()
            if (TextUtils.isEmpty(personName)) {
                "请输入个人姓名".toast()
                return false
            }
        }
        if (binding.rbCompany.isChecked) { // 选择的是单位开票
            companyName = binding.etComponyName.text.toString()
            if (TextUtils.isEmpty(companyName)) {
                "请输入个公司名称".toast()
                return false
            }
            taxpayerName = binding.etComponyTaxpayer.text.toString()
            if (TextUtils.isEmpty(taxpayerName)) {
                "请输入纳税人识别号".toast()
                return false
            }
        }
        if (TextUtils.isEmpty(invoiceInfo.addressId)) {
            "请选择发票邮寄地址".toast()
            return false
        }
        return true
    }

    override fun observe() {
        super.observe()
        viewModel.invoiceLiveData.observe(this, Observer {
            this.finish()
        })
        viewModel.addressList.observe(this, Observer {
            // 获取默认地址
            it?.let { list ->
                list.forEach { cu ->
                    if (cu.isDefault == 1) {
                        addressBeanItem = cu
                    }
                }
            }
            if (addressBeanItem == null) {
                try {
                    if (it?.size!! > 0) {
                        it.apply {
                            addressBeanItem = get(0)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            invoiceInfo.addressId = addressBeanItem?.addressId.toString()
            invoiceInfo.addressInfo = addressBeanItem?.getAddress().toString()
            invoiceInfo.userInfo = addressBeanItem?.getUserInfo().toString()
            binding.tvAddress.text = invoiceInfo.addressInfo
            binding.tvUserInfo.text = invoiceInfo.userInfo


        })
        LiveDataBus.get().with(LiveDataBusKey.INVOICE_ADDRESS_SUCCESS, String::class.java)
            .observe(this, Observer {
                it?.let {
                    // TODO 更换地址。
                    localAddressObserve(it)
                }

            })
    }

    private fun localAddressObserve(addressInfoJson: String) {
        val address = Gson().fromJson(addressInfoJson, ShopAddressInfoBean::class.java)
        invoiceInfo.addressId = address?.addressId.toString()
        invoiceInfo.addressInfo = address?.getAddress().toString()
        invoiceInfo.userInfo = address?.getUserInfos().toString()
        binding.tvAddress.text = invoiceInfo.addressInfo
        binding.tvUserInfo.text = invoiceInfo.userInfo
    }
}