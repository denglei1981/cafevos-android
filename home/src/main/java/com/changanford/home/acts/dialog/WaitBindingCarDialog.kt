package com.changanford.home.acts.dialog

import android.app.Activity
import android.content.Context
import android.view.Gravity


import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.changanford.common.bean.CityX
import com.changanford.common.bean.Province
import com.changanford.common.net.*
import com.changanford.common.widget.picker.CityPicker
import com.changanford.common.widget.picker.annotation.AddressMode.PROVINCE_CITY
import com.changanford.common.widget.picker.contract.OnAddressPickedListener
import com.changanford.common.widget.picker.entity.CityEntity
import com.changanford.common.widget.picker.entity.CountyEntity
import com.changanford.common.widget.picker.entity.ProvinceEntity
import com.changanford.home.R
import com.changanford.home.acts.adapter.HomeActsScreenItemAdapter
import com.changanford.home.acts.adapter.HomeActsTypeItemAdapter
import com.changanford.home.base.BaseAppCompatDialog
import com.changanford.home.bean.ScreenData
import com.changanford.home.callback.ICallback
import com.changanford.home.data.EnumBean
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.DialogHomeActsScreenBinding
import com.changanford.home.databinding.LayoutWaitBindingCarBinding
import com.changanford.home.util.launchWithCatch


class WaitBindingCarDialog(var acts: Context, private val lifecycleOwner: LifecycleOwner) :
    BaseAppCompatDialog(acts, Gravity.CENTER) {
    lateinit var mDatabind: LayoutWaitBindingCarBinding
    lateinit var callback: ICallback
    var cityName: String = ""
    var cityId: String = ""


    constructor(acts: Context, lifecycleOwner: LifecycleOwner, callback: ICallback) : this(
        acts,
        lifecycleOwner
    ) {
        this.callback = callback
        mDatabind = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_wait_binding_car,
            null,
            false
        )
        setContentView(mDatabind.root)
        initView()
        initData()
    }


    override fun initAd() {

    }

    fun initView() {

    }

    fun initData() {


    }


    var provinces = ArrayList<ProvinceEntity>()

    fun getData() {
        lifecycleOwner.launchWithCatch {
            val body = HashMap<String, String>()
            body["district"] = "true"
            val rkey = getRandomKey()
            ApiClient.apiService.getAllCity(body.header(rkey), body.body(rkey)).onSuccess {
                provinces.clear()
                it?.forEach { p ->
                    val province = ProvinceEntity(p.province.regionId, p.province.regionName)
                    val citys = ArrayList<CityEntity>()
                    p.citys.forEach { c ->
                        val city = CityEntity(c.city.regionId, c.city.regionName)
                        val countys = ArrayList<CountyEntity>()
                        c.district.forEach { d ->
                            val county = CountyEntity(d.regionId, d.regionName)
                            countys.add(county)
                        }
                        city.countyList = countys
                        citys.add(city)
                    }
                    province.cityList = citys
                    provinces.add(province)
                }
            }
        }
    }


}