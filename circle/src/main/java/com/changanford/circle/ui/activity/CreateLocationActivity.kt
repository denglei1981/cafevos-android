package com.changanford.circle.ui.activity

import android.Manifest
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import com.changanford.circle.R
import com.changanford.circle.databinding.ActvityCreateLocationBinding
import com.changanford.circle.viewmodel.CreateLocationViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.common.widget.picker.CityPicker
import com.changanford.common.widget.picker.annotation.AddressMode
import com.changanford.common.widget.picker.contract.OnAddressPickedListener
import com.changanford.common.widget.picker.entity.CityEntity
import com.changanford.common.widget.picker.entity.CountyEntity
import com.changanford.common.widget.picker.entity.ProvinceEntity
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.bean.Permissions
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig

class CreateLocationActivity :
    BaseActivity<ActvityCreateLocationBinding, CreateLocationViewModel>() {


    var cityId: String = ""
    var provinces = ArrayList<ProvinceEntity>()
    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.commTitleBar, this)
    }

    override fun initData() {
        binding.cArea.etTxt.setOnClickListener {
            if (provinces.size == 0) {
                viewModel.getData()
            } else {
                chooseCity()
            }
        }
        binding.tvConsle.setOnClickListener {
            // 加个弹窗
            onBackPressed()
        }
        binding.tvCommit.setOnClickListener {
            // 完成
            if (canCreate()) {
                viewModel.addPostData(
                    addrName = locationName,
                    provinceName,
                    cityName,
                    countyName,
                    detailLocation,
                    lat,
                    lon,
                    realCity = realCity
                )
            }
        }
        soulPermission()
//        viewModel.getLocation()
    }

    var locationName: String = ""
    var detailLocation: String = ""
    var provinceName: String = ""
    var cityName: String = ""
    var countyName: String = ""
    var lat: Double = -1.0
    var lon: Double = -1.0
    var realCity:String=""

    private fun canCreate(): Boolean {
        locationName = binding.cLocaiton.etContent // 位置名字
        if (TextUtils.isEmpty(locationName)) {
            "请输入位置名称".toast()
            return false
        }
        val cAreaName = binding.cArea.etTxt.text.toString() // 省市区
        if (TextUtils.isEmpty(cAreaName)) {
            "请选择省市区".toast()
            return false
        }
        detailLocation = binding.layoutDetailLocaiton.etContent // 详细地址
        if (TextUtils.isEmpty(detailLocation)) {
            "请输入详细地址".toast()
            return false
        }
        if (lat == -1.0 || lon == -1.0) {
            "请开启定位".toast()
            soulPermission()
            return false
        }
        return true
    }

    override fun observe() {
        super.observe()
        viewModel.provincesListLiveData.observe(this, Observer {
            provinces = it
            chooseCity()
        })
        viewModel.locationLotLonLiveData.observe(this, Observer {
            lat = it.lat
            lon = it.lon
            realCity=it.realCity
        })
        viewModel.createLocationLiveData.observe(this, Observer {
            LiveDataBus.get().with(LiveDataBusKey.CREATE_COLSE_LOCATION, Boolean::class.java)
                .postValue(true)
            this.finish()
        })
    }


    var cityPicker: CityPicker? = null
    fun chooseCity() { // 选择城市。。
        cityPicker = CityPicker(this).apply {
            setAddressMode(provinces, AddressMode.PROVINCE_CITY_COUNTY)
            //
            setDefaultValue("重庆市", "重庆市", "渝中区")
        }
        cityPicker?.setOnAddressPickedListener(object : OnAddressPickedListener {
            override fun onAddressPicked(
                province: ProvinceEntity?,
                city: CityEntity?,
                county: CountyEntity?
            ) {
                var cityA: String = ""
                //选择城市的回调。
                province?.let {
//                    body["province"] = it.code
//                    body["provinceName"] = "${it.name}"
                    provinceName = it.name
                    cityA = it.name
                }
                city?.let {
                    if (cityA != it.name) {
                        cityA += it.name
                    }
                    cityName = it.name
                    cityId = it.code
                }
                county?.let {
                    cityA += it.name
                    countyName = it.name
                }
                binding.cArea.etTxt.text = cityA
            }
        })
        cityPicker?.show()
    }

    fun soulPermission() {
        val permissions = Permissions.build(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        val success = {
            viewModel.getLocation()
        }
        val fail = {
            toastShow("拒绝定位,创建位置将失败")
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
//        SoulPermission.getInstance()
//            .checkAndRequestPermission(
//                Manifest.permission.ACCESS_FINE_LOCATION,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
//                object : CheckRequestPermissionListener {
//                    override fun onPermissionOk(permission: Permission) {
//                        viewModel.getLocation()
//                    }
//
//                    override fun onPermissionDenied(permission: Permission) {
//                        toastShow("拒绝定位,创建位置将失败")
//                    }
//                })
    }

    override fun onBackPressed() {
        if (hasEdit()) {
            createLevel()
        } else {
            super.onBackPressed()
        }


    }

    fun hasEdit(): Boolean {
        locationName = binding.cLocaiton.etContent // 位置名字
        if (!TextUtils.isEmpty(locationName)) {
            return true
        }
        val cAreaName = binding.cArea.etTxt.text.toString() // 省市区
        if (!TextUtils.isEmpty(cAreaName)) {
            return true
        }
        detailLocation = binding.layoutDetailLocaiton.etContent // 详细地址
        if (!TextUtils.isEmpty(detailLocation)) {
            return true
        }

        return false
    }

    fun createLevel() {
        QuickPopupBuilder.with(this)
            .contentView(R.layout.pop_create_location_warn)
            .config(
                QuickPopupConfig()
                    .gravity(Gravity.CENTER)
                    .withClick(R.id.btn_comfir, View.OnClickListener {

                    }, true)
                    .withClick(R.id.btn_cancel, View.OnClickListener {
                        finish()
                    }, true)
            )
            .show()
    }
}