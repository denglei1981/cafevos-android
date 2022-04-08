package com.changanford.my.ui

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MineUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.widget.picker.CityPicker
import com.changanford.common.widget.picker.contract.OnAddressPickedListener
import com.changanford.common.widget.picker.entity.CityEntity
import com.changanford.common.widget.picker.entity.CountyEntity
import com.changanford.common.widget.picker.entity.ProvinceEntity
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiEditAddressBinding
import com.changanford.my.viewmodel.AddressViewModel
import com.jakewharton.rxbinding4.widget.textChanges


/**
 *  文件名：EditAddressUI
 *  创建者: zcy
 *  创建日期：2021/9/24 10:23
 *  描述: TODO
 *  修改描述：TODO
 */

@Route(path = ARouterMyPath.EditAddressUI, extras = 100)
class EditAddressUI : BaseMineUI<UiEditAddressBinding, AddressViewModel>(),
    OnAddressPickedListener {
    var provinces = ArrayList<ProvinceEntity>()

    var isShowPicker: Boolean = false

    var body = HashMap<String, Any>() //提交数据

    var addressBean: AddressBeanItem? = null

    private var isChooseAdd: Int = 0

    override fun initView() {
        intent.extras?.getInt(RouterManger.KEY_TO_ITEM, 0)?.let {
            isChooseAdd = it
            binding.save.text = if (it != 0) "保存并使用" else "保存"
        }

        binding.addressToolbar.toolbarTitle.text = "添加地址"
        binding.addressToolbar.toolbar.setNavigationOnClickListener { back() }
        body["addressId"] = "0"
        body["isDefault"] = 0
        intent.extras?.getSerializable(RouterManger.KEY_TO_OBJ)?.let {
            addressBean = it as AddressBeanItem
            addressBean?.let { bean ->
                binding.etAddressName.setText(bean.consignee)
                binding.etAddressPhone.setText(bean.phone)
                binding.tvAddressCity.text =
                    "${bean.provinceName}${bean.cityName}${bean.districtName}"
                binding.etAddressDetail.setText(bean.addressName)
                binding.addressSwitch.isChecked = bean.isDefault == 1
                body["addressId"] = bean.addressId
                body["province"] = bean.province
                body["provinceName"] = bean.provinceName
                body["city"] = bean.city
                body["cityName"] = bean.cityName
                body["district"] = bean.district
                body["districtName"] = bean.districtName
            }
        }

        viewModel.allCity.observe(this, Observer {
            provinces.clear()
            it?.forEach { p ->
                var province = ProvinceEntity(p.province.regionId, p.province.regionName)
                var citys = ArrayList<CityEntity>()
                p.citys.forEach { c ->
                    var city = CityEntity(c.city.regionId, c.city.regionName)
                    var countys = ArrayList<CountyEntity>()
                    c.district.forEach { d ->
                        var county = CountyEntity(d.regionId, d.regionName)
                        countys.add(county)
                    }
                    city.countyList = countys
                    citys.add(city)
                }
                province.cityList = citys
                provinces.add(province)
                if (isShowPicker) {
                    showPicker()
                }
            }
        })

        var name = binding.etAddressName.textChanges()
        var phone = binding.etAddressPhone.textChanges()
        var city = binding.tvAddressCity.textChanges()
        var detailAddress = binding.etAddressDetail.textChanges()

        io.reactivex.rxjava3.core.Observable.combineLatest(
            name,
            phone,
            city,
            detailAddress,
            io.reactivex.rxjava3.functions.Function4<CharSequence, CharSequence, CharSequence, CharSequence, Boolean> { t1, t2, t3, t4 ->
                body["consignee"] = t1.toString()
                body["phone"] = t2.toString()
                body["addressName"] = t4.toString()
                binding.tvTextNum.text = "${t4.length}"
                t1.isNotEmpty() && t2.isNotEmpty() && t3.isNotEmpty() && t4.isNotEmpty()
            }).subscribe {
            binding.save.isEnabled = it
        }

        binding.tvAddressCityLayout.setOnClickListener {
            if (provinces.size == 0) {
                isShowPicker = true
                viewModel.getAllCity(true)
                return@setOnClickListener
            }
            showPicker()
        }

        binding.addressSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            body["isDefault"] = if (isChecked) "1" else "0"
        }
        binding.save.setOnClickListener {
            var name: String = binding.etAddressName.text.toString().trim()
            var phone: String = binding.etAddressPhone.text.toString().trim()
            var address: String = binding.etAddressDetail.text.toString().trim()
            if (MineUtils.compileExChar(name)) {
                showToast("姓名不能输入特殊字符")
                return@setOnClickListener
            }
            if (!phone.startsWith("1") || phone.length != 11) {
                showToast("请输入正确的手机号")
                return@setOnClickListener
            }
            if (MineUtils.compileExChar(address)) {
                showToast("详细地址不能输入特殊字符")
                return@setOnClickListener
            }
            viewModel.saveAddress(body) {
                it.onSuccess {
                    showToast("地址保存成功")
                    if (isChooseAdd == 2) {// 发票
                        it?.let { item ->
                            LiveDataBus.get().with(LiveDataBusKey.INVOICE_ADDRESS_SUCCESS)
                                .postValue(JSON.toJSON(item).toString())//H5回调数据
                            val intent = Intent()
                            intent.putExtra("addressBeanItem", item)
                            setResult(Activity.RESULT_OK, intent)
                        }
                    }
                    if(isChooseAdd!=0&&isChooseAdd!=2){
                        it?.let { item ->
                            LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS)
                                .postValue(JSON.toJSON(item).toString())//H5回调数据
                            val intent = Intent()
                            intent.putExtra("addressBeanItem", item)
                            setResult(Activity.RESULT_OK, intent)
                        }
                    }
                    //直接返回
                    LiveDataBus.get().with(LiveDataBusKey.MINE_UPDATE_ADDRESS)
                        .postValue(isChooseAdd == 0)
                    finish()
                }
                it.onWithMsgFailure {
                    it?.let {
                        showToast(it)
                    }
                }
            }
        }
    }

    private fun showPicker() {
        val picker = CityPicker(this)
            .apply {
                setAddressMode(provinces)
//                    setDefaultValue("重庆市", "重庆市", "渝中区")
                addressBean?.let {
                    setDefaultValue(
                        "${it.provinceName}",
                        "${it.cityName}",
                        "${it.districtName}"
                    )
                }
                setOnAddressPickedListener(this@EditAddressUI)
            }
        picker.show()
        isShowPicker = false
    }

    override fun initData() {
        viewModel.getAllCity()
    }

    override fun onAddressPicked(
        province: ProvinceEntity?,
        city: CityEntity?,
        county: CountyEntity?
    ) {
        var cityA: String = ""
        province?.let {
            body["province"] = it.code
            body["provinceName"] = "${it.name}"
            cityA = it.name
        }
        city?.let {
            body["city"] = it.code
            body["cityName"] = "${it.name}"
            cityA += it.name
        }
        county?.let {
            body["district"] = it.code
            body["districtName"] = "${it.name}"
            cityA += it.name
        }
        binding.tvAddressCity.text = cityA
    }
}